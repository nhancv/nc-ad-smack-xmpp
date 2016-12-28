package com.nhancv.xmpp;

import com.nhancv.xmpp.model.ParticipantPresence;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jxmpp.util.XmppStringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
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
    private static final String TAG = XmppUtil.class.getSimpleName();

    public static boolean isReceived(String xmlMessage) {
        return xmlMessage.matches("(.*)<received xmlns='urn:xmpp:receipts' id='(.*)'/>(.*)");
    }

    public static boolean isForwarded(String xmlMessage) {
        return xmlMessage.matches("(.*)<forwarded xmlns='urn:xmpp:forward:0'>(.*)");
    }

    public static boolean isMe(Message message) {
        return XmppStringUtils.parseBareJid(message.getFrom()).contains(XmppStringUtils.parseBareJid(message.getTo()));
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

    public static boolean isGroupMessage(String xmlMessage) {
        return xmlMessage.matches("<message(.*)from='(.*)@conference.(.*)' (.*)type='groupchat'>(.*)<body>(.*)</body>(.*)");
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

    public static Message parseForwardedMessage(Message message) {
        try {
            String xmlMessage = message.toXML().toString();

            if (isMe(message) && isForwarded(xmlMessage)) {
                Message msg = new Message();

                ExtensionElement isSent = message.getExtension("sent", "urn:xmpp:carbons:2");
                ExtensionElement isReceived = message.getExtension("received", "urn:xmpp:carbons:2");
                String xml;
                if (isSent != null) {
                    xml = isSent.toXML().toString();
                } else if (isReceived != null) {
                    xml = isReceived.toXML().toString();
                } else {
                    return null;
                }

                Element element = XmppUtil.getRootElement(xml);

                if (element != null) {
                    Node messageNode = element.getFirstChild().getFirstChild();
                    boolean isBody = messageNode.getFirstChild().getNodeName().equals("body");
                    boolean isRecv = messageNode.getFirstChild().getNodeName().equals("received");
                    NamedNodeMap namedNodeMap = messageNode.getAttributes();
                    String from = namedNodeMap.getNamedItem("from").getNodeValue();
                    String to = namedNodeMap.getNamedItem("to").getNodeValue();
                    String id = namedNodeMap.getNamedItem("id").getNodeValue();

                    if (isBody) {
                        String body = null;
                        try {
                            Node bodyNode = messageNode.getFirstChild().getFirstChild();
                            if (bodyNode != null)
                                body = bodyNode.getNodeValue();
                        } catch (Exception ignored) {
                        }
                        msg.setType(Message.Type.chat);
                        msg.setBody(body);
                    } else if (isRecv) {
                        id = messageNode.getFirstChild().getAttributes().getNamedItem("id").getNodeValue();
                    }
                    msg.setFrom(XmppStringUtils.parseBareJid(from));
                    msg.setTo(XmppStringUtils.parseBareJid(to));
                    msg.setStanzaId(id);

                }
                return msg;
            } else {
                return null;
            }
        } catch (Exception e) {
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
