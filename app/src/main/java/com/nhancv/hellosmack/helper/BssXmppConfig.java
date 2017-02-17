package com.nhancv.hellosmack.helper;

import com.nhancv.xmpp.IXmppConfig;
import com.nhancv.xmpp.XmppPresenter;

import org.jxmpp.util.XmppStringUtils;

/**
 * Created by nhancao on 2/17/17.
 */

public class BssXmppConfig implements IXmppConfig {

    private static final String PREFIX_JID = "fitaccess.demo";
    private static final String HOST = "local.beesightsoft.com";
    private static final String DOMAIN = "local.beesightsoft.com";
    private static final int PORT = 6001;

    /**
     * Return complete jid from local part.<br>
     * LocalPart is "test", the result returned would be "test@domain"
     *
     * @param localJid
     * @return
     */
    public static String getCompletedJid(String localJid) {
        return String.format("%s@%s", localJid, DOMAIN);
    }

    /**
     * Get jid format by user id
     * for the userId "1", "fitaccess.demo1@local.beesightsoft.com" would be returned
     *
     * @param userId
     * @return
     */
    public static String getJidFromUserId(String userId) {
        return String.format("%s%s@%s", PREFIX_JID, userId, DOMAIN);
    }

    /**
     * Get local part format by user id
     * for the userId "1", "fitaccess.demo1" would be returned
     *
     * @param userId
     * @return
     */
    public static String getLocalPartFromUserId(String userId) {
        return String.format("%s%s", PREFIX_JID, userId);
    }

    /**
     * Get user id from jid
     * For "PREFIX_JID1111@DOMAIN", "1111" would be returned
     *
     * @param jid
     * @return
     */
    public static String getUserIdFromJid(String jid) {
        String res = XmppStringUtils.parseLocalpart(XmppPresenter.getInstance().getCurrentUser());
        if (res == null) {
            if (jid != null) {
                return jid.replace(PREFIX_JID, "").replace("@" + DOMAIN, "");
            }
        } else {
            return res.replace(PREFIX_JID, "");
        }
        return null;
    }

    @Override
    public String getHost() {
        return HOST;
    }

    @Override
    public String getDomain() {
        return DOMAIN;
    }

    @Override
    public Integer getPort() {
        return PORT;
    }

}
