package com.drprog.simplerssreader.sync;

/**
 * Class for representation of item of Rss feed.
 */
public class RssItem {
    public Long pubDate;
    public String title;
    public String author;
    public String imageUrl;
    public String linkUrl;

    public RssItem(Long pubDate, String title, String author, String imageUrl, String linkUrl) {
        this.pubDate = pubDate;
        this.title = title;
        this.author = author;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RssItem rssItem = (RssItem) o;

        if (author != null ? !author.equals(rssItem.author) : rssItem.author != null) return false;
        if (imageUrl != null ? !imageUrl.equals(rssItem.imageUrl) : rssItem.imageUrl != null) {
            return false;
        }
        if (linkUrl != null ? !linkUrl.equals(rssItem.linkUrl) : rssItem.linkUrl != null)
            return false;
        if (pubDate != null ? !pubDate.equals(rssItem.pubDate) : rssItem.pubDate != null)
            return false;
        if (title != null ? !title.equals(rssItem.title) : rssItem.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = pubDate != null ? pubDate.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (imageUrl != null ? imageUrl.hashCode() : 0);
        result = 31 * result + (linkUrl != null ? linkUrl.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RssItem{" +
                "pubDate=" + pubDate +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", linkUrl='" + linkUrl + '\'' +
                '}';
    }
}
