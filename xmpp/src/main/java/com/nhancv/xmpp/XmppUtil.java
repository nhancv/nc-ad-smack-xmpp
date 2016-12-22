package com.nhancv.xmpp;

import com.nhancv.xmpp.model.ParticipantPresence;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by nhancao on 12/15/16.
 */

public class XmppUtil {

    public static boolean isDelivered(String xmlMessage) {
        return xmlMessage.matches("(.*)<received xmlns='urn:xmpp:receipts' id='(.*)'/>(.*)");
    }

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

    public static boolean isOfflineStorage(String xmlMessage) {
        return xmlMessage.matches("(.*)<delay xmlns='urn:xmpp:delay'(.*)</delay>(.*)");
    }

    public static ChatState getChatState(String xmlMessage) {
        if (isActive(xmlMessage)) return ChatState.active;
        if (isComposing(xmlMessage)) return ChatState.composing;
        if (isPause(xmlMessage)) return ChatState.paused;
        if (isInActive(xmlMessage)) return ChatState.inactive;
        if (isGone(xmlMessage)) return ChatState.gone;
        return null;
    }

    public static Element getRootElement(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xml)));
            return document.getDocumentElement();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ParticipantPresence getParticipantPresence(Presence presence) {
        try {
            String xml = presence.getExtension("x", "http://jabber.org/protocol/muc#user").toXML().toString();
            Element element = XmppUtil.getRootElement(xml);
            if (element != null) {
                NamedNodeMap namedNodeMap = element.getElementsByTagName("item").item(0).getAttributes();
                return new ParticipantPresence(
                        namedNodeMap.getNamedItem("jid").getNodeValue(),
                        namedNodeMap.getNamedItem("role").getNodeValue()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
