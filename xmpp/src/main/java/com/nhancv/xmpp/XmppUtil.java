package com.nhancv.xmpp;

import org.jivesoftware.smackx.chatstates.ChatState;

/**
 * Created by nhancao on 12/15/16.
 */

public class XmppUtil {


    public static boolean isActive(String xmlMessage) {
        return xmlMessage.matches("(.*)<" + ChatState.active.name() + "(.*)/>(.*)");
    }

    public static boolean isComposing(String xmlMessage) {
        return xmlMessage.matches("(.*)<" + ChatState.composing.name() + "(.*)/>(.*)");
    }

    public static boolean isPause(String xmlMessage) {
        return xmlMessage.matches("(.*)<" + ChatState.paused.name() + "(.*)/>(.*)");
    }

    public static boolean isInActive(String xmlMessage) {
        return xmlMessage.matches("(.*)<" + ChatState.inactive.name() + "(.*)/>(.*)");
    }

    public static boolean isGone(String xmlMessage) {
        return xmlMessage.matches("(.*)<" + ChatState.gone.name() + "(.*)/>(.*)");
    }

    public static boolean isMessage(String xmlMessage) {
        return xmlMessage.matches("(.*)<body>(.*)</body>(.*)");
    }

    public static ChatState getChatState(String xmlMessage) {
        if (isActive(xmlMessage)) return ChatState.active;
        if (isComposing(xmlMessage)) return ChatState.composing;
        if (isPause(xmlMessage)) return ChatState.paused;
        if (isInActive(xmlMessage)) return ChatState.inactive;
        if (isGone(xmlMessage)) return ChatState.gone;
        return null;
    }
}
