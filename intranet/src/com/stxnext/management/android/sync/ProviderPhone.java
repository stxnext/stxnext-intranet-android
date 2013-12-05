package com.stxnext.management.android.sync;
public class ProviderPhone {
        private String displayName;
        private String phoneNumber;
        private Long id;
        private Long contactId;
        private String type;
        private String label;

        private String numberToUpdate;

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setContactId(Long contactId) {
            this.contactId = contactId;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public Long getId() {
            return id;
        }

        public Long getContactId() {
            return contactId;
        }

        public String getType() {
            return type;
        }

        public String getLabel() {
            return label;
        }

        public String getNumberToUpdate() {
            return numberToUpdate;
        }

        public void setNumberToUpdate(String numberToUpdate) {
            this.numberToUpdate = numberToUpdate;
        }

    }