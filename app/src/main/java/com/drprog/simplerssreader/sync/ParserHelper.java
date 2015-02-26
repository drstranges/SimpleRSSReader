package com.drprog.simplerssreader.sync;

import android.content.ContentValues;
import android.content.Context;

import com.drprog.simplerssreader.data.DataProvider;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on 26.02.2015.
 */
public class ParserHelper {

    private static final String TAG_ITEM = "item";
    private static final String TAG_TITLE = "title";
    private static final String TAG_LINK = "link";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_PUB_DATE = "pubDate";
    private static final String TAG_AUTHOR = "author";

    private static final String IMG_REGEX = "<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";
    private static final Pattern IMG_PATTERN = Pattern.compile(IMG_REGEX);

    private static final SimpleDateFormat PUB_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

    public static ContentValues[] parseXML(Context context, String response)
            throws XmlPullParserException {
        XmlPullParserFactory xmlParserFactory = XmlPullParserFactory.newInstance();
        XmlPullParser xmlParser = xmlParserFactory.newPullParser();
        xmlParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        StringReader stringReader = new StringReader(response);
        xmlParser.setInput(stringReader);
        ContentValues[] resultCV = parse(xmlParser);
        stringReader.close();
        return resultCV;
    }

    private static ContentValues[] parse(XmlPullParser xmlParser) {
        Vector<ContentValues> vector = new Vector<ContentValues>();
        int event;
        String text=null;

        String description=null;

        String pubDate = null;
        String title = null;
        String author = null;
        String imageUrl = null;
        String linkUrl = null;

        try {
            event = xmlParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String name=xmlParser.getName();
                switch (event){
                    case XmlPullParser.START_TAG:
                        if (name.equalsIgnoreCase(TAG_ITEM)) {
                            text = null;
                            description = null;
                            pubDate = null;
                            title = null;
                            author = null;
                            imageUrl = null;
                            linkUrl = null;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        text = xmlParser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if(name.equalsIgnoreCase(TAG_TITLE)){
                            title = text;
                        }
                       else if(name.equalsIgnoreCase(TAG_LINK)){
                            linkUrl = text;
                        }
                        else if(name.equalsIgnoreCase(TAG_PUB_DATE)){
                            pubDate = text;
                        }
                        else if(name.equalsIgnoreCase(TAG_AUTHOR)){
                            author = text;
                        }
                        else if(name.equalsIgnoreCase(TAG_DESCRIPTION)){
                            description = text;
                        }else if (name.equalsIgnoreCase(TAG_ITEM)){
                            Long dateMs = null;
                            try {
                                Date date = PUB_DATE_FORMAT.parse(pubDate);
                                dateMs = date.getTime();
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            if (description != null){
                                Matcher matcher = IMG_PATTERN.matcher(description);
                                if (matcher.find()) {
                                    imageUrl = matcher.group(1);
                                }
                            }
                            ContentValues cv = DataProvider.buildCV(dateMs,title, linkUrl, author, imageUrl);
                            if (cv != null){
                                vector.add(cv);
                            }
                        }else{
                            //do nothing
                        }
                        break;
                }
                event = xmlParser.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        ContentValues[] cvArray = new ContentValues[vector.size()];
        return vector.toArray(cvArray);
    }
}
