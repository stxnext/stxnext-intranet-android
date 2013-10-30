
package com.stxnext.management.android.dto.local;

import java.util.Date;

import ch.boye.httpclientandroidlib.cookie.Cookie;
import ch.boye.httpclientandroidlib.impl.cookie.BasicClientCookie;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stxnext.management.android.dto.AbstractMessage;

public class LocalCookie extends AbstractMessage {

    @Expose
    @SerializedName("domain")
    String domain;
    @Expose
    @SerializedName("expiryDate")
    Long expiryDate;
    @Expose
    @SerializedName("name")
    String name;
    @Expose
    @SerializedName("path")
    String path;
    @Expose
    @SerializedName("value")
    String value;
    @Expose
    @SerializedName("version")
    Integer version;

    public LocalCookie(Cookie cookie) {
        this.domain = cookie.getDomain();
        this.expiryDate = cookie.getExpiryDate() != null ? cookie.getExpiryDate().getTime() : null;
        this.name = cookie.getName();
        this.path = cookie.getPath();
        this.value = cookie.getValue();
        this.version = cookie.getVersion();
    }

    public Cookie convertToRealCookie() {
        BasicClientCookie real = new BasicClientCookie(name, value);
        if (domain != null)
            real.setDomain(domain);
        if (expiryDate != null)
            real.setExpiryDate(new Date(expiryDate));
        if (path != null)
            real.setPath(path);
        if (version != null)
            real.setVersion(version);
        return real;
    }

    public String getDomain() {
        return domain;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getValue() {
        return value;
    }

    public Integer getVersion() {
        return version;
    }

}
