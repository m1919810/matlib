package me.matl114.matlib.nmsUtils.chat;

import me.matl114.matlib.nmsMirror.chat.ChatEnum;

public interface ContentBuilder extends BuildResult {

    public ContentBuilder clone();

    default ContentBuilder toImmutable() {
        Object value = toNMS();
        return new ContentBuilder() {
            @Override
            public Object toNMS() {
                return value;
            }

            @Override
            public ContentBuilder clone() {
                return this;
            }

            public ContentBuilder toImmutable() {
                return this;
            }

            @Override
            public boolean isImmutable() {
                return true;
            }
        };
    }

    static ContentBuilder EMPTY = new ContentBuilder() {
        @Override
        public ContentBuilder clone() {
            return this;
        }

        @Override
        public Object toNMS() {
            return ChatEnum.PLAIN_TEXT_EMPTY;
        }

        @Override
        public boolean isImmutable() {
            return true;
        }

        public ContentBuilder toImmutable() {
            return this;
        }
    };

    static ContentBuilder empty() {
        return EMPTY;
    }
}
