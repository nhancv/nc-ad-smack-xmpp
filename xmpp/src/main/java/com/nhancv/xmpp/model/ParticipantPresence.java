package com.nhancv.xmpp.model;

/**
 * Created by nhancao on 12/22/16.
 */

public class ParticipantPresence {

    private String jid;
    private Role role;

    public ParticipantPresence(String jid, String roleStr) {
        this.jid = jid;
        this.role = Role.getRole(roleStr);
    }

    public ParticipantPresence(String jid, Role role) {
        this.jid = jid;
        this.role = role;
    }

    public String getJid() {
        return jid;
    }

    public Role getRole() {
        return role;
    }

    public enum Role {
        PARTICIPANT("participant"),
        NONE("none"),
        OTHER("other");
        private String role;

        Role(String role) {
            this.role = role;
        }

        public static Role getRole(String roleStr) {
            if (roleStr.equals(Role.PARTICIPANT.getRole())) {
                return Role.PARTICIPANT;
            } else if (roleStr.equals(Role.NONE.getRole())) {
                return Role.NONE;
            } else {
                return Role.OTHER;
            }
        }

        public String getRole() {
            return role;
        }

    }


}
