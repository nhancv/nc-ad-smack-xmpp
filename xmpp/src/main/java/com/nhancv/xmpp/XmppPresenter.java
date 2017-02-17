package com.nhancv.xmpp;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.nhancv.xmpp.listener.XmppListener;
import com.nhancv.xmpp.model.BaseBody;
import com.nhancv.xmpp.model.BaseError;
import com.nhancv.xmpp.model.BaseMessage;
import com.nhancv.xmpp.model.BaseNotify;
import com.nhancv.xmpp.model.BaseRoom;
import com.nhancv.xmpp.model.BaseRoster;
import com.nhancv.xmpp.model.ParticipantPresence;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromMatchesFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smackx.carbons.CarbonManager;
import org.jivesoftware.smackx.chatstates.ChatStateManager;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.DefaultParticipantStatusListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.Occupant;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jxmpp.util.XmppStringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nhancao on 12/13/16.
 */

public class XmppPresenter implements IXmppPresenter {
    private static final String TAG = XmppPresenter.class.getSimpleName();

    private static XmppPresenter instance;
    private static IXmppConfig xmppConfig;
    private Map<String, BaseRoster> userListMap;
    private Map<String, List<BaseMessage>> messageListMap;
    private Map<String, BaseRoom> roomListMap;

    private IXmppConnector xmppConnector;
    private RosterListener baseRosterListener;
    private OfflineMessageManager offlineMessageManager;

    public XmppPresenter() {
        this.roomListMap = new LinkedHashMap<>();
        this.messageListMap = new LinkedHashMap<>();
        this.userListMap = new LinkedHashMap<>();
    }

    public static XmppPresenter getInstance() {
        if (instance == null) instance = new XmppPresenter();
        return instance;
    }

    public static void resetInstance() {
        if (instance != null) {
            instance.logout();
            instance.userListMap = null;
            instance.messageListMap = null;
            instance.roomListMap = null;

            instance.xmppConnector = null;
            instance.baseRosterListener = null;
            instance.offlineMessageManager = null;
            instance = null;
        }
    }

    public static void setXmppConfig(IXmppConfig iXmppConfig) {
        xmppConfig = iXmppConfig;
    }

    @Override
    public IXmppConnector getXmppConnector() {
        return xmppConnector;
    }

    @Override
    public void login(String userJid, String passwod, XmppListener.IXmppLoginListener loginConnectionListener)
            throws IOException, SmackException, XMPPException {
        IXmppCredential xmppCredential = new XmppCredential(userJid, passwod);
        xmppConnector = new XmppConnector(xmppConfig);
        xmppConnector.setupLoginConnection(xmppCredential, loginConnectionListener);
        xmppConnector.createConnection();
    }

    @Override
    public void createUser(String userJid, String password, XmppListener.IXmppCreateListener createConnectionListener)
            throws XMPPException, IOException, SmackException {
        xmppConnector = new XmppConnector(xmppConfig);
        xmppConnector.setupCreateConnection(new XmppListener.IXmppConnListener() {
            @Override
            public void success() {
                AccountManager accountManager = AccountManager.getInstance(xmppConnector.getConnection());
                accountManager.sensitiveOperationOverInsecureConnection(true);
                try {
                    if (accountManager.supportsAccountCreation()) {
                        accountManager.createAccount(userJid, password);
                        createConnectionListener.createSuccess();
                    } else {
                        Log.e(TAG, "success: Server not support create account");
                        createConnectionListener.createError(new Exception("Server not support create account"));
                    }
                } catch (XMPPException.XMPPErrorException e) {
                    if (e.getXMPPError().getType() == XMPPError.Type.CANCEL) {
                        createConnectionListener.createError(new Exception("User exists"));
                    } else {
                        createConnectionListener.createError(e);
                    }
                } catch (SmackException.NoResponseException | SmackException.NotConnectedException e) {
                    createConnectionListener.createError(e);
                }
            }

            @Override
            public void error(Exception ex) {
                createConnectionListener.createError(ex);
            }
        });
        xmppConnector.createConnection();
    }

    @Override
    public void logout() {
        logout(true);
    }

    @Override
    public void logout(boolean clearCache) {
        if (clearCache) {
            userListMap.clear();
            messageListMap.clear();
            roomListMap.clear();
        }
        if (isConnected()) {
            try {
                xmppConnector.terminalConnection();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void connectionListenerRegister(ConnectionListener connectionListener) {
        if (isConnected()) {
            xmppConnector.getConnection().addConnectionListener(connectionListener);
        }
    }

    @Override
    public void enableCarbonMessage() {
        if (isConnected()) {
            try {
                CarbonManager cm = CarbonManager.getInstanceFor(xmppConnector.getConnection());
                if (cm.isSupportedByServer()) {
                    cm.enableCarbons();
                    cm.sendCarbonsEnabled(true);
                }
            } catch (XMPPException | SmackException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void disableCarbonMessage() {
        if (isConnected()) {
            try {
                CarbonManager cm = CarbonManager.getInstanceFor(xmppConnector.getConnection());
                if (cm.isSupportedByServer()) {
                    cm.disableCarbons();
                    cm.sendCarbonsEnabled(false);
                }
            } catch (XMPPException | SmackException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isConnected() {
        return xmppConnector != null && xmppConnector.getConnection() != null && xmppConnector.getConnection().isConnected();
    }

    @Override
    public void sendStanza(@NonNull Stanza packet) {
        if (isConnected()) {
            try {
                xmppConnector.getConnection().sendStanza(packet);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sendInviteRequest(String rosterJid) {
        if (isConnected()) {
            Presence subscribe = new Presence(Presence.Type.subscribe);
            subscribe.setTo(rosterJid);
            sendStanza(subscribe);
        }
    }

    @Override
    public void acceptInviteRequest(String rosterJid) {
        if (isConnected()) {
            //Accept subscribe request
            Presence subscribe = new Presence(Presence.Type.subscribed);
            subscribe.setTo(rosterJid);
            sendStanza(subscribe);
            //Sent subscribe
            subscribe.setType(Presence.Type.subscribe);
            sendStanza(subscribe);
            //Update presence
            setAvailablePresence();
        }
    }

    @Override
    public void sendUnFriendRequest(String rosterJid) {
        if (isConnected()) {
            //Send unfriend request
            RosterPacket subscribe = new RosterPacket();
            subscribe.setType(IQ.Type.set);
            RosterPacket.Item item = new RosterPacket.Item(rosterJid, null);
            item.setItemType(RosterPacket.ItemType.remove);
            subscribe.addRosterItem(item);
            sendStanza(subscribe);
        }
    }

    @Override
    public void acceptUnFriendRequest(String rosterJid) {
        if (isConnected()) {
            //Update presence
            setUnavailablePresence();
            //Accept unsubscribed
            Presence subscribe = new Presence(Presence.Type.unsubscribed);
            subscribe.setTo(rosterJid);
            sendStanza(subscribe);
            //Sent unsubscribe
            subscribe.setType(Presence.Type.unsubscribe);
            sendStanza(subscribe);

        }
    }

    @Override
    public boolean isFriends(String rosterJid) {
        return (isConnected() && getRoster(rosterJid) != null);
    }

    @Override
    public void removeAsyncStanzaListener(StanzaListener listener) {
        if (isConnected()) {
            xmppConnector.getConnection().removeAsyncStanzaListener(listener);
        }
    }

    @Override
    public void removeAsyncStanzaListener(StanzaPackageType packetListener) {
        if (isConnected()) {
            removeAsyncStanzaListener(packetListener.getStanzaListener());
        }
    }

    @Override
    public void addAsyncStanzaListener(StanzaPackageType packetListener) {
        if (isConnected()) {
            addAsyncStanzaListener(packetListener.getStanzaListener(),
                    new StanzaTypeFilter(packetListener.getPacketType()));
        }
    }

    @Override
    public void addMessageStanzaListener(XmppListener.IXmppCallback<BaseMessage> messageStanzaListener) {
        if (isConnected()) {
            StanzaPackageType messagePackageType = new StanzaPackageType(packet -> {
                if (packet instanceof Message) {
                    Message message = (Message) packet;
                    Log.e(TAG, "addMessageStanzaListener: " + message.toXML().toString());
                    if (message.getFrom() != null && XmppUtil.isGroupMessage(message.toXML().toString())) {
                        String roomId = XmppStringUtils.parseBareJid(message.getFrom());
                        List<BaseMessage> messageList = new ArrayList<>();
                        if (messageListMap.containsKey(roomId)) {
                            messageList = messageListMap.get(roomId);
                        }
                        BaseMessage baseMessage = new BaseMessage(message);
                        messageList.add(baseMessage);
                        messageListMap.put(roomId, messageList);
                        BaseRoom baseRoom = roomListMap.get(roomId);
                        if (baseRoom != null) baseRoom.setLastMessage(message.getBody());

                        messageStanzaListener.callback(baseMessage);

                    } else {

                        Message parseForwardedMessage = XmppUtil.parseForwardedMessage(message);

                        if (parseForwardedMessage != null) {
                            if (XmppUtil.isReceived(message.toXML().toString())) {
                                String jid = XmppStringUtils.parseBareJid(parseForwardedMessage.getFrom());

                                if (messageListMap.containsKey(jid)) {
                                    List<BaseMessage> baseMessages = messageListMap.get(jid);
                                    for (BaseMessage baseMessage : baseMessages) {
                                        if (baseMessage.getMessage().getStanzaId().equals(parseForwardedMessage.getStanzaId())) {
                                            baseMessage.setSentType(BaseMessage.SentType.DELIVERED);

                                            messageStanzaListener.callback(baseMessage);
                                            break;
                                        }
                                    }
                                }

                            } else if (XmppUtil.isMessage(parseForwardedMessage.toXML().toString())) {
                                String jid = XmppStringUtils.parseBareJid(parseForwardedMessage.getTo());

                                List<BaseMessage> messageList = new ArrayList<>();
                                if (messageListMap.containsKey(jid)) {
                                    messageList = messageListMap.get(jid);
                                }
                                BaseMessage baseMessage = new BaseMessage(parseForwardedMessage);
                                messageList.add(baseMessage);
                                messageListMap.put(jid, messageList);

                                messageStanzaListener.callback(baseMessage);
                            }
                        } else {
                            String xml = message.toXML().toString();
                            if (XmppUtil.isMessage(xml)) {
                                String jid = XmppStringUtils.parseBareJid(message.getFrom());

                                if (XmppUtil.isNotify(message.getBody())) {

                                    BaseNotify notify = new Gson().fromJson(message.getBody(), BaseNotify.class);
                                    String mid = notify.getContent().replace("-mid-", "").replace("-end-", "");
                                    //Update seen status
                                    List<BaseMessage> messageList = new ArrayList<>();
                                    if (messageListMap.containsKey(jid)) {
                                        messageList = messageListMap.get(jid);
                                    }

                                    for (BaseMessage baseMessage : messageList) {
                                        if (baseMessage.getMessage().getStanzaId().equals(mid)) {
                                            baseMessage.setSentType(BaseMessage.SentType.SEEN);

                                            messageListMap.put(jid, messageList);
                                            messageStanzaListener.callback(baseMessage);
                                            break;
                                        }
                                    }

                                } else {

                                    //Push message to list
                                    List<BaseMessage> messageList = new ArrayList<>();
                                    if (messageListMap.containsKey(jid)) {
                                        messageList = messageListMap.get(jid);
                                    }
                                    BaseMessage baseMessage = new BaseMessage(message);
                                    messageList.add(baseMessage);
                                    messageListMap.put(jid, messageList);

                                    messageStanzaListener.callback(baseMessage);

                                }
                            }
                        }
                    }
                }
            }, Message.class);

            //Listener for delivery message
            getDeliveryReceiptManager().addReceiptReceivedListener((fromJid, toJid, receiptId, receipt) -> {
                String shortJid = XmppStringUtils.parseBareJid(fromJid);
                if (messageListMap.containsKey(shortJid)) {
                    List<BaseMessage> baseMessages = messageListMap.get(shortJid);
                    for (BaseMessage baseMessage : baseMessages) {
                        if (baseMessage.getMessage().getStanzaId().equals(receiptId)) {
                            baseMessage.setSentType(BaseMessage.SentType.DELIVERED);

                            messageStanzaListener.callback(baseMessage);
                            break;
                        }
                    }
                }
            });

            addAsyncStanzaListener(messagePackageType);
        }
    }

    @Override
    public void addAsyncStanzaListener(StanzaListener packetListener, StanzaFilter
            packetFilter) {
        if (isConnected()) {
            xmppConnector.getConnection().addAsyncStanzaListener(packetListener, packetFilter);
        }
    }

    @Override
    public void addListStanzaListener(List<StanzaPackageType> packetListeners) {
        if (isConnected()) {
            for (StanzaPackageType listener : packetListeners) {
                addAsyncStanzaListener(listener.getStanzaListener(), new StanzaTypeFilter(listener.getPacketType()));
            }
        }
    }

    @Override
    public void setAutoAcceptSubscribe() {
        if (isConnected()) {
            StanzaPackageType autoAcceptSubscribe = new StanzaPackageType(packet -> {
                if (packet instanceof Presence) {
                    Presence presence = (Presence) packet;
                    if (presence.getType() != null) {
                        switch (presence.getType()) {
                            case subscribe:
                                acceptInviteRequest(presence.getFrom());
                                break;
                            case unsubscribe:
                                acceptUnFriendRequest(presence.getFrom());
                                break;

                        }
                    }
                }
            }, Stanza.class);

            addAsyncStanzaListener(autoAcceptSubscribe.getStanzaListener(),
                    new StanzaTypeFilter(autoAcceptSubscribe.getPacketType()));
        }
    }

    @Override
    public Chat openChatSession(StanzaListener listener, String toJid) {
        if (isConnected()) {
            addAsyncStanzaListener(listener,
                    new AndFilter(new StanzaTypeFilter(Message.class),
                            new FromMatchesFilter(toJid, true)));

            return getChatManager().createChat(toJid);
        } else {
            return null;
        }

    }

    @Override
    public void closeChatSession(StanzaListener listener) {
        removeAsyncStanzaListener(listener);
    }

    @Override
    public MultiUserChatManager getMultiUserChatManager() {
        if (isConnected()) {
            return MultiUserChatManager.getInstanceFor(xmppConnector.getConnection());
        }
        return null;
    }

    @Override
    public DeliveryReceiptManager getDeliveryReceiptManager() {
        if (isConnected()) {
            return DeliveryReceiptManager.getInstanceFor(xmppConnector.getConnection());
        }
        return null;
    }

    @Override
    public OfflineMessageManager getOfflineMessageManager() {
        if (isConnected()) {
            if (offlineMessageManager == null) {
                offlineMessageManager = new OfflineMessageManager(xmppConnector.getConnection());
            }
            return offlineMessageManager;
        }
        return null;
    }

    @Override
    public ChatStateManager getChatStateManager() {
        if (isConnected()) {
            return ChatStateManager.getInstance(xmppConnector.getConnection());
        }
        return null;
    }

    @Override
    public ChatManager getChatManager() {
        return ChatManager.getInstanceFor(xmppConnector.getConnection());
    }

    @Override
    public ChatRoomStateManager getChatRoomStateManager() {
        return ChatRoomStateManager.getInstance(xmppConnector.getConnection());
    }

    @Override
    public List<BaseMessage> getMessageList(String jid) {
        jid = XmppStringUtils.parseBareJid(jid);
        if (!messageListMap.containsKey(jid)) {
            messageListMap.put(jid, new ArrayList<>());
        }
        return messageListMap.get(jid);
    }

    @Override
    public void clearMessageList(String jid) {
        messageListMap.put(jid, new ArrayList<>());
    }

    @Override
    public void updateMessageList(String jid, List<BaseMessage> messageList) {
        List<BaseMessage> msgList = getMessageList(jid);
        if (msgList == null) {
            msgList = new ArrayList<>();
        } else {
            Collections.sort(msgList, (o1, o2) -> {
                BaseBody baseBody1 = BaseBody.fromJson(o1.getMessage().getBody());
                BaseBody baseBody2 = BaseBody.fromJson(o2.getMessage().getBody());
                return (int) (baseBody1.getTimestamp() - baseBody2.getTimestamp());
            });
        }
        if (messageList == null || messageList.isEmpty()) return;

        LinkedHashMap<String, BaseMessage> map = new LinkedHashMap<>();
        for (BaseMessage baseMessage : messageList) {
            map.put(baseMessage.getMessage().getStanzaId(), baseMessage);
        }
        for (BaseMessage baseMessage : msgList) {
            map.put(baseMessage.getMessage().getStanzaId(), baseMessage);
        }

        msgList.clear();
        msgList.addAll(map.values());
        Collections.sort(msgList, (o1, o2) -> {
            BaseBody baseBody1 = BaseBody.fromJson(o1.getMessage().getBody());
            BaseBody baseBody2 = BaseBody.fromJson(o2.getMessage().getBody());
            return (int) (baseBody1.getTimestamp() - baseBody2.getTimestamp());
        });
        messageListMap.put(jid, msgList);
    }

    @Override
    public List<BaseRoom> getRoomList() {
        return new ArrayList<>(roomListMap.values());
    }

    @Override
    public BaseRoom getRoom(String roomId) {
        return roomListMap.get(roomId);
    }

    @Override
    public List<BaseRoster> getCurrentRosterList() {
        return new ArrayList<>(userListMap.values());
    }

    @Override
    public BaseRoster getRoster(String rosterJid) {
        return userListMap.get(XmppStringUtils.parseBareJid(rosterJid));
    }

    @Override
    public Roster setupRosterList(@NonNull RosterListener rosterListener) {
        return setupRosterList(rosterListener, null);
    }

    @Override
    public Roster setupRosterList
            (@NonNull XmppListener.IXmppCallback<BaseRoster> updateListener) {
        return setupRosterList(null, updateListener);
    }

    @Override
    public Roster setupRosterList(RosterListener
                                          rosterListener, XmppListener.IXmppCallback<BaseRoster> updateListener) {
        if (isConnected()) {
            Roster roster = Roster.getInstanceFor(xmppConnector.getConnection());
            Collection<RosterEntry> entries = roster.getEntries();
            Presence presence;

            for (RosterEntry entry : entries) {
                String jid = XmppStringUtils.parseBareJid(entry.getUser());
                presence = roster.getPresence(entry.getUser());
                if (presence.getType() == Presence.Type.unavailable) {
                    sendInviteRequest(jid);
                }
                userListMap.put(jid, new BaseRoster(entry.getUser(), presence));
            }

            roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
            if (baseRosterListener == null) {
                baseRosterListener = new RosterListener() {
                    @Override
                    public void entriesAdded(Collection<String> addresses) {
                        for (String item : addresses) {
                            Presence presence = roster.getPresence(item);
                            String jid = XmppStringUtils.parseBareJid(item);
                            BaseRoster baseRoster = new BaseRoster(item, presence);
                            userListMap.put(jid, baseRoster);
                            if (updateListener != null) updateListener.callback(baseRoster);

                        }
                        if (rosterListener != null)
                            rosterListener.entriesAdded(addresses);
                    }

                    @Override
                    public void entriesUpdated(Collection<String> addresses) {
                        for (String item : addresses) {
                            for (BaseRoster baseRoster : userListMap.values()) {
                                String jid = XmppStringUtils.parseBareJid(baseRoster.getJid());
                                if (item.contains(jid)) {
                                    Presence presence = roster.getPresence(item);
                                    baseRoster.setPresence(presence);
                                    if (updateListener != null)
                                        updateListener.callback(baseRoster);
                                    break;
                                }
                            }
                        }
                        if (rosterListener != null)
                            rosterListener.entriesUpdated(addresses);
                    }

                    @Override
                    public void entriesDeleted(Collection<String> addresses) {
                        for (String item : addresses) {
                            for (BaseRoster baseRoster : userListMap.values()) {
                                String jid = XmppStringUtils.parseBareJid(baseRoster.getJid());
                                if (item.contains(jid)) {
                                    userListMap.values().remove(baseRoster);
                                    if (updateListener != null)
                                        updateListener.callback(baseRoster);
                                    break;
                                }
                            }
                        }
                        if (rosterListener != null)
                            rosterListener.entriesDeleted(addresses);
                    }

                    @Override
                    public void presenceChanged(Presence presence) {
                        for (BaseRoster baseRoster : userListMap.values()) {
                            String jid = XmppStringUtils.parseBareJid(baseRoster.getJid());
                            if (presence.getFrom().contains(jid)) {
                                baseRoster.setPresence(presence);
                                if (updateListener != null) updateListener.callback(baseRoster);
                                break;
                            }
                        }
                        if (rosterListener != null)
                            rosterListener.presenceChanged(presence);
                    }
                };
            } else {
                roster.removeRosterListener(baseRosterListener);
            }
            roster.addRosterListener(baseRosterListener);

            if (updateListener != null) updateListener.callback(null);

            return roster;
        }
        return null;
    }

    @Override
    public String getCurrentUser() {
        if (isConnected()) {
            return xmppConnector.getConnection().getUser();
        }
        return null;
    }

    @Override
    public void updatePresence(Presence.Mode presenceMode, String status) {
        if (isConnected()) {
            Presence p = new Presence(Presence.Type.available, status, 42, presenceMode);
            sendStanza(p);
        }
    }

    @Override
    public void setAvailablePresence() {
        if (isConnected()) {
            sendStanza(new Presence(Presence.Type.available));
        }
    }

    @Override
    public void setUnavailablePresence() {
        if (isConnected()) {
            sendStanza(new Presence(Presence.Type.unavailable));
        }
    }


    @Override
    public MultiUserChat createGroupChat(String groupName, String description, String
            roomId, String ownerJid,
                                         XmppListener.CreateGroupListener createGroupListener,
                                         XmppListener.ParticipantListener participantListener)
            throws XMPPException.XMPPErrorException, SmackException {
        if (isConnected()) {
            String serviceName = "conference." + XmppStringUtils.parseDomain(ownerJid);
            String roomFullId = roomId + "@" + serviceName;

            MultiUserChatManager manager = getMultiUserChatManager();
            MultiUserChat chatRoom = manager.getMultiUserChat(roomFullId);

            if (chatRoom.isJoined()) {
                createGroupListener.joined(chatRoom);
            } else {
                if (chatRoom.createOrJoin(ownerJid)) {
                    // We successfully created a new room
                    Form cfgForm = chatRoom.getConfigurationForm();
                    Form form = chatRoom.getConfigurationForm().createAnswerForm();
                    for (FormField field : cfgForm.getFields()) {
                        if (!FormField.Type.hidden.name().equals(field.getType().name())
                                && field.getVariable() != null) {
                            form.setDefaultAnswer(field.getVariable());
                        }
                    }
                    form.setAnswer(FormField.FORM_TYPE, "http://jabber.org/protocol/muc#roomconfig");
                    form.setAnswer("muc#roomconfig_roomdesc", description);
                    form.setAnswer("muc#roomconfig_roomname", groupName);
                    form.setAnswer("muc#roomconfig_publicroom", true);
                    form.setAnswer("muc#roomconfig_changesubject", true);
                    form.setAnswer("muc#roomconfig_persistentroom", true);

                    List<String> maxUsers = new ArrayList<>();
                    maxUsers.add("100");
                    form.setAnswer("muc#roomconfig_maxusers", maxUsers);

                    List<String> cast_values = new ArrayList<>();
                    cast_values.add("moderator");
                    cast_values.add("participant");
                    cast_values.add("visitor");
                    form.setAnswer("muc#roomconfig_presencebroadcast", cast_values);

                    chatRoom.sendConfigurationForm(form);
                    mucListenerRegister(chatRoom, participantListener);
                    createGroupListener.created(chatRoom);

                } else {
                    // We need to leave the room since it seems that the room already existed
                    chatRoom.leave();
                    createGroupListener.exists(chatRoom);
                }
            }
            return chatRoom;
        }
        return null;
    }

    @Override
    public void leaveRoom(MultiUserChat muc) {
        if (muc != null && isConnected()) {
            try {
                muc.leave();
                refreshRoomListMap();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public BaseError joinRoom(MultiUserChat muc, XmppListener.ParticipantListener
            participantListener, String nickName) {
        if (muc != null && isConnected()) {
            try {
                muc.join(nickName);

                mucListenerRegister(muc, participantListener);
                return new BaseError();
            } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
                return new BaseError(true, e.getMessage(), e);
            }
        }
        return new BaseError(true, "MultiUserChat is null");
    }

    @Override
    public void mucListenerRegister(MultiUserChat muc,
                                    final XmppListener.ParticipantListener participantListener) {
        muc.addParticipantListener(presence -> {
            ParticipantPresence participant = XmppUtil.getParticipantPresence(presence);
            if (participant != null) {
                refreshRoomListMap();
                Log.d(TAG, "new participant accepted: " + participant.getJid());
                participantListener.processPresence(participant);
            }
        });

        muc.addParticipantStatusListener(new DefaultParticipantStatusListener() {
            @Override
            public void joined(String participant) {
                refreshRoomListMap();
                String roomId = XmppStringUtils.parseBareJid(participant);
                String userJid = XmppStringUtils.parseResource(participant);
                Log.d(TAG, "joined: roomId " + roomId + " userJid: " + userJid);
                participantListener.processPresence(new ParticipantPresence(userJid, ParticipantPresence.Role.PARTICIPANT));
            }

            @Override
            public void left(String participant) {
                refreshRoomListMap();
                String roomId = XmppStringUtils.parseBareJid(participant);
                String userJid = XmppStringUtils.parseResource(participant);
                Log.d(TAG, "left: roomId " + roomId + " userJid: " + userJid);
                participantListener.processPresence(new ParticipantPresence(userJid, ParticipantPresence.Role.NONE));
            }
        });
        refreshRoomListMap();
    }

    @Override
    public void refreshRoomListMap() {
        MultiUserChatManager manager = XmppPresenter.getInstance().getMultiUserChatManager();
        roomListMap.clear();
        for (String roomId : manager.getJoinedRooms()) {
            MultiUserChat muc = manager.getMultiUserChat(roomId);
            List<Occupant> members = new ArrayList<>();
            for (String o : muc.getOccupants()) {
                Occupant occupant = muc.getOccupant(o);
                members.add(occupant);
            }
            roomListMap.put(roomId, new BaseRoom(muc, members));
        }
    }

}
