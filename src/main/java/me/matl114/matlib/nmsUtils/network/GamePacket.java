package me.matl114.matlib.nmsUtils.network;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.internel.ObfManager;
import me.matl114.matlib.utils.version.Version;

public enum GamePacket {
    // later!!!!
    //    CLIENTBOUND_CUSTOM_PAYLOAD(true, PacketFlow.S2C, "custom_payload"),
    //    CLIENTBOUND_CUSTOM_REPORT_DETAILS(true, PacketFlow.S2C, "custom_report_details"),
    //    CLIENTBOUND_DISCONNECT(true, PacketFlow.S2C, "disconnect"),
    //    CLIENTBOUND_KEEP_ALIVE(true, PacketFlow.S2C, "keep_alive"),
    //    CLIENTBOUND_PING(true, PacketFlow.S2C, "ping"),
    //    CLIENTBOUND_RESOURCE_PACK_POP(true, PacketFlow.S2C, "resource_pack_pop"),
    //    CLIENTBOUND_RESOURCE_PACK_PUSH(true, PacketFlow.S2C, "resource_pack_push"),
    //    CLIENTBOUND_SERVER_LINKS(true, PacketFlow.S2C, "server_links"),
    //    CLIENTBOUND_STORE_COOKIE(true, PacketFlow.S2C, "store_cookie"),
    //    CLIENTBOUND_TRANSFER(true, PacketFlow.S2C, "transfer"),
    //    CLIENTBOUND_UPDATE_TAGS(true, PacketFlow.S2C, "update_tags"),

    SERVERBOUND_CLIENT_INFORMATION(GamePacketHandler.COMMON, PacketFlow.C2S, "client_information"),
    SERVERBOUND_CUSTOM_PAYLOAD(GamePacketHandler.COMMON, PacketFlow.C2S, "custom_payload"),
    SERVERBOUND_KEEP_ALIVE(GamePacketHandler.COMMON, PacketFlow.C2S, "keep_alive"),
    SERVERBOUND_PONG(GamePacketHandler.COMMON, PacketFlow.C2S, "pong"),
    SERVERBOUND_RESOURCE_PACK(GamePacketHandler.COMMON, PacketFlow.C2S, "resource_pack"),

    CLIENTBOUND_BUNDLE(PacketFlow.S2C, "bundle"),
    CLIENTBOUND_BUNDLE_DELIMITER(PacketFlow.S2C, "bundle_delimiter"),
    CLIENTBOUND_ADD_ENTITY(PacketFlow.S2C, "add_entity"),
    CLIENTBOUND_ADD_EXPERIENCE_ORB(PacketFlow.S2C, "add_experience_orb"),
    CLIENTBOUND_ANIMATE(PacketFlow.S2C, "animate"),
    CLIENTBOUND_AWARD_STATS(PacketFlow.S2C, "award_stats"),
    CLIENTBOUND_BLOCK_CHANGED_ACK(PacketFlow.S2C, "block_changed_ack"),
    CLIENTBOUND_BLOCK_DESTRUCTION(PacketFlow.S2C, "block_destruction"),
    CLIENTBOUND_BLOCK_ENTITY_DATA(PacketFlow.S2C, "block_entity_data"),
    CLIENTBOUND_BLOCK_EVENT(PacketFlow.S2C, "block_event"),
    CLIENTBOUND_BLOCK_UPDATE(PacketFlow.S2C, "block_update"),
    CLIENTBOUND_BOSS_EVENT(PacketFlow.S2C, "boss_event"),
    CLIENTBOUND_CHANGE_DIFFICULTY(PacketFlow.S2C, "change_difficulty"),

    CLIENTBOUND_CHUNKS_BIOMES(PacketFlow.S2C, "chunks_biomes"),
    CLIENTBOUND_CLEAR_TITLES(PacketFlow.S2C, "clear_titles"),
    CLIENTBOUND_COMMAND_SUGGESTIONS(PacketFlow.S2C, "command_suggestions"),
    CLIENTBOUND_COMMANDS(PacketFlow.S2C, "commands"),
    CLIENTBOUND_CONTAINER_CLOSE(PacketFlow.S2C, "container_close"),
    CLIENTBOUND_CONTAINER_SET_CONTENT(PacketFlow.S2C, "container_set_content"),
    CLIENTBOUND_CONTAINER_SET_DATA(PacketFlow.S2C, "container_set_data"),
    CLIENTBOUND_CONTAINER_SET_SLOT(PacketFlow.S2C, "container_set_slot"),
    CLIENTBOUND_COOLDOWN(PacketFlow.S2C, "cooldown"),
    CLIENTBOUND_CUSTOM_CHAT_COMPLETIONS(PacketFlow.S2C, "custom_chat_completions"),
    CLIENTBOUND_DAMAGE_EVENT(PacketFlow.S2C, "damage_event"),

    CLIENTBOUND_DELETE_CHAT(PacketFlow.S2C, "delete_chat"),
    CLIENTBOUND_DISGUISED_CHAT(PacketFlow.S2C, "disguised_chat"),
    CLIENTBOUND_ENTITY_EVENT(PacketFlow.S2C, "entity_event"),
    CLIENTBOUND_EXPLODE(PacketFlow.S2C, "explode"),
    CLIENTBOUND_FORGET_LEVEL_CHUNK(PacketFlow.S2C, "forget_level_chunk"),
    CLIENTBOUND_GAME_EVENT(PacketFlow.S2C, "game_event"),
    CLIENTBOUND_HORSE_SCREEN_OPEN(PacketFlow.S2C, "horse_screen_open"),
    CLIENTBOUND_HURT_ANIMATION(PacketFlow.S2C, "hurt_animation"),
    CLIENTBOUND_INITIALIZE_BORDER(PacketFlow.S2C, "initialize_border"),
    CLIENTBOUND_LEVEL_CHUNK_WITH_LIGHT(PacketFlow.S2C, "level_chunk_with_light"),
    CLIENTBOUND_LEVEL_EVENT(PacketFlow.S2C, "level_event"),
    CLIENTBOUND_LEVEL_PARTICLES(PacketFlow.S2C, "level_particles"),
    CLIENTBOUND_LIGHT_UPDATE(PacketFlow.S2C, "light_update"),
    CLIENTBOUND_LOGIN(PacketFlow.S2C, "login"),
    CLIENTBOUND_MAP_ITEM_DATA(PacketFlow.S2C, "map_item_data"),
    CLIENTBOUND_MERCHANT_OFFERS(PacketFlow.S2C, "merchant_offers"),
    CLIENTBOUND_MOVE_ENTITY_POS(PacketFlow.S2C, "move_entity_pos"),
    CLIENTBOUND_MOVE_ENTITY_POS_ROT(PacketFlow.S2C, "move_entity_pos_rot"),
    CLIENTBOUND_MOVE_ENTITY_ROT(PacketFlow.S2C, "move_entity_rot"),
    CLIENTBOUND_MOVE_VEHICLE(PacketFlow.S2C, "move_vehicle"),
    CLIENTBOUND_OPEN_BOOK(PacketFlow.S2C, "open_book"),
    CLIENTBOUND_OPEN_SCREEN(PacketFlow.S2C, "open_screen"),
    CLIENTBOUND_OPEN_SIGN_EDITOR(PacketFlow.S2C, "open_sign_editor"),
    CLIENTBOUND_PLACE_GHOST_RECIPE(PacketFlow.S2C, "place_ghost_recipe"),
    CLIENTBOUND_PLAYER_ABILITIES(PacketFlow.S2C, "player_abilities"),
    CLIENTBOUND_PLAYER_CHAT(PacketFlow.S2C, "player_chat"),
    CLIENTBOUND_PLAYER_COMBAT_END(PacketFlow.S2C, "player_combat_end"),
    CLIENTBOUND_PLAYER_COMBAT_ENTER(PacketFlow.S2C, "player_combat_enter"),
    CLIENTBOUND_PLAYER_COMBAT_KILL(PacketFlow.S2C, "player_combat_kill"),
    CLIENTBOUND_PLAYER_INFO_REMOVE(PacketFlow.S2C, "player_info_remove"),
    CLIENTBOUND_PLAYER_INFO_UPDATE(PacketFlow.S2C, "player_info_update"),
    CLIENTBOUND_PLAYER_LOOK_AT(PacketFlow.S2C, "player_look_at"),
    CLIENTBOUND_PLAYER_POSITION(PacketFlow.S2C, "player_position"),

    // remove because of protocol change
    CLIENTBOUND_RECIPE_BOOK(PacketFlow.S2C, "recipe_book", null, Version.v1_21_R2),

    CLIENTBOUND_REMOVE_ENTITIES(PacketFlow.S2C, "remove_entities"),
    CLIENTBOUND_REMOVE_MOB_EFFECT(PacketFlow.S2C, "remove_mob_effect"),
    CLIENTBOUND_RESPAWN(PacketFlow.S2C, "respawn"),
    CLIENTBOUND_ROTATE_HEAD(PacketFlow.S2C, "rotate_head"),
    CLIENTBOUND_SECTION_BLOCKS_UPDATE(PacketFlow.S2C, "section_blocks_update"),
    CLIENTBOUND_SELECT_ADVANCEMENTS_TAB(PacketFlow.S2C, "select_advancements_tab"),
    CLIENTBOUND_SERVER_DATA(PacketFlow.S2C, "server_data"),
    CLIENTBOUND_SET_ACTION_BAR_TEXT(PacketFlow.S2C, "set_action_bar_text"),
    CLIENTBOUND_SET_BORDER_CENTER(PacketFlow.S2C, "set_border_center"),
    CLIENTBOUND_SET_BORDER_LERP_SIZE(PacketFlow.S2C, "set_border_lerp_size"),
    CLIENTBOUND_SET_BORDER_SIZE(PacketFlow.S2C, "set_border_size"),
    CLIENTBOUND_SET_BORDER_WARNING_DELAY(PacketFlow.S2C, "set_border_warning_delay"),
    CLIENTBOUND_SET_BORDER_WARNING_DISTANCE(PacketFlow.S2C, "set_border_warning_distance"),
    CLIENTBOUND_SET_CAMERA(PacketFlow.S2C, "set_camera"),
    CLIENTBOUND_SET_CHUNK_CACHE_CENTER(PacketFlow.S2C, "set_chunk_cache_center"),
    CLIENTBOUND_SET_CHUNK_CACHE_RADIUS(PacketFlow.S2C, "set_chunk_cache_radius"),
    CLIENTBOUND_SET_DEFAULT_SPAWN_POSITION(PacketFlow.S2C, "set_default_spawn_position"),
    CLIENTBOUND_SET_DISPLAY_OBJECTIVE(PacketFlow.S2C, "set_display_objective"),
    CLIENTBOUND_SET_ENTITY_DATA(PacketFlow.S2C, "set_entity_data"),
    CLIENTBOUND_SET_ENTITY_LINK(PacketFlow.S2C, "set_entity_link"),
    CLIENTBOUND_SET_ENTITY_MOTION(PacketFlow.S2C, "set_entity_motion"),
    CLIENTBOUND_SET_EQUIPMENT(PacketFlow.S2C, "set_equipment"),
    CLIENTBOUND_SET_EXPERIENCE(PacketFlow.S2C, "set_experience"),
    CLIENTBOUND_SET_HEALTH(PacketFlow.S2C, "set_health"),
    CLIENTBOUND_SET_HELD_SLOT(PacketFlow.S2C, "set_held_slot"),
    CLIENTBOUND_SET_OBJECTIVE(PacketFlow.S2C, "set_objective"),
    CLIENTBOUND_SET_PASSENGERS(PacketFlow.S2C, "set_passengers"),
    CLIENTBOUND_SET_PLAYER_TEAM(PacketFlow.S2C, "set_player_team"),
    CLIENTBOUND_SET_SCORE(PacketFlow.S2C, "set_score"),
    CLIENTBOUND_SET_SIMULATION_DISTANCE(PacketFlow.S2C, "set_simulation_distance"),
    CLIENTBOUND_SET_SUBTITLE_TEXT(PacketFlow.S2C, "set_subtitle_text"),
    CLIENTBOUND_SET_TIME(PacketFlow.S2C, "set_time"),
    CLIENTBOUND_SET_TITLE_TEXT(PacketFlow.S2C, "set_title_text"),
    CLIENTBOUND_SET_TITLES_ANIMATION(PacketFlow.S2C, "set_titles_animation"),
    CLIENTBOUND_SOUND_ENTITY(PacketFlow.S2C, "sound_entity"),
    CLIENTBOUND_SOUND(PacketFlow.S2C, "sound"),

    CLIENTBOUND_STOP_SOUND(PacketFlow.S2C, "stop_sound"),
    CLIENTBOUND_SYSTEM_CHAT(PacketFlow.S2C, "system_chat"),
    CLIENTBOUND_TAB_LIST(PacketFlow.S2C, "tab_list"),
    CLIENTBOUND_TAG_QUERY(PacketFlow.S2C, "tag_query"),
    CLIENTBOUND_TAKE_ITEM_ENTITY(PacketFlow.S2C, "take_item_entity"),
    CLIENTBOUND_TELEPORT_ENTITY(PacketFlow.S2C, "teleport_entity"),

    CLIENTBOUND_UPDATE_ADVANCEMENTS(PacketFlow.S2C, "update_advancements"),
    CLIENTBOUND_UPDATE_ATTRIBUTES(PacketFlow.S2C, "update_attributes"),
    CLIENTBOUND_UPDATE_MOB_EFFECT(PacketFlow.S2C, "update_mob_effect"),
    CLIENTBOUND_UPDATE_RECIPES(PacketFlow.S2C, "update_recipes"),

    SERVERBOUND_ACCEPT_TELEPORTATION(PacketFlow.C2S, "accept_teleportation"),
    SERVERBOUND_BLOCK_ENTITY_TAG_QUERY(PacketFlow.C2S, "block_entity_tag_query"),

    SERVERBOUND_CHANGE_DIFFICULTY(PacketFlow.C2S, "change_difficulty"),
    SERVERBOUND_CHAT_ACK(PacketFlow.C2S, "chat_ack"),

    SERVERBOUND_CHAT_COMMAND_SIGNED(PacketFlow.C2S, "chat_command_signed"),
    SERVERBOUND_CHAT(PacketFlow.C2S, "chat"),
    SERVERBOUND_CHAT_SESSION_UPDATE(PacketFlow.C2S, "chat_session_update"),

    SERVERBOUND_CLIENT_COMMAND(PacketFlow.C2S, "client_command"),

    SERVERBOUND_COMMAND_SUGGESTION(PacketFlow.C2S, "command_suggestion"),

    SERVERBOUND_CONTAINER_BUTTON_CLICK(PacketFlow.C2S, "container_button_click"),
    SERVERBOUND_CONTAINER_CLICK(PacketFlow.C2S, "container_click"),
    SERVERBOUND_CONTAINER_CLOSE(PacketFlow.C2S, "container_close"),

    SERVERBOUND_EDIT_BOOK(PacketFlow.C2S, "edit_book"),
    SERVERBOUND_ENTITY_TAG_QUERY(PacketFlow.C2S, "entity_tag_query"),
    SERVERBOUND_INTERACT(PacketFlow.C2S, "interact"),
    SERVERBOUND_JIGSAW_GENERATE(PacketFlow.C2S, "jigsaw_generate"),
    SERVERBOUND_LOCK_DIFFICULTY(PacketFlow.C2S, "lock_difficulty"),
    SERVERBOUND_MOVE_PLAYER_POS(PacketFlow.C2S, "move_player_pos"),
    SERVERBOUND_MOVE_PLAYER_POS_ROT(PacketFlow.C2S, "move_player_pos_rot"),
    SERVERBOUND_MOVE_PLAYER_ROT(PacketFlow.C2S, "move_player_rot"),
    SERVERBOUND_MOVE_PLAYER_STATUS_ONLY(PacketFlow.C2S, "move_player_status_only"),
    SERVERBOUND_MOVE_VEHICLE(PacketFlow.C2S, "move_vehicle"),
    SERVERBOUND_PADDLE_BOAT(PacketFlow.C2S, "paddle_boat"),
    SERVERBOUND_PICK_ITEM(PacketFlow.C2S, "pick_item"),
    SERVERBOUND_PLACE_RECIPE(PacketFlow.C2S, "place_recipe"),
    SERVERBOUND_PLAYER_ABILITIES(PacketFlow.C2S, "player_abilities"),
    SERVERBOUND_PLAYER_ACTION(PacketFlow.C2S, "player_action"),
    SERVERBOUND_PLAYER_COMMAND(PacketFlow.C2S, "player_command"),
    SERVERBOUND_PLAYER_INPUT(PacketFlow.C2S, "player_input"),
    SERVERBOUND_RECIPE_BOOK_CHANGE_SETTINGS(PacketFlow.C2S, "recipe_book_change_settings"),
    SERVERBOUND_RECIPE_BOOK_SEEN_RECIPE(PacketFlow.C2S, "recipe_book_seen_recipe"),
    SERVERBOUND_RENAME_ITEM(PacketFlow.C2S, "rename_item"),
    SERVERBOUND_SEEN_ADVANCEMENTS(PacketFlow.C2S, "seen_advancements"),
    SERVERBOUND_SELECT_TRADE(PacketFlow.C2S, "select_trade"),
    SERVERBOUND_SET_BEACON(PacketFlow.C2S, "set_beacon"),
    SERVERBOUND_SET_CARRIED_ITEM(PacketFlow.C2S, "set_carried_item"),
    SERVERBOUND_SET_COMMAND_BLOCK(PacketFlow.C2S, "set_command_block"),
    SERVERBOUND_SET_COMMAND_MINECART(PacketFlow.C2S, "set_command_minecart"),
    SERVERBOUND_SET_CREATIVE_MODE_SLOT(PacketFlow.C2S, "set_creative_mode_slot"),
    SERVERBOUND_SET_JIGSAW_BLOCK(PacketFlow.C2S, "set_jigsaw_block"),
    SERVERBOUND_SET_STRUCTURE_BLOCK(PacketFlow.C2S, "set_structure_block"),
    SERVERBOUND_SIGN_UPDATE(PacketFlow.C2S, "sign_update"),
    SERVERBOUND_SWING(PacketFlow.C2S, "swing"),
    SERVERBOUND_TELEPORT_TO_ENTITY(PacketFlow.C2S, "teleport_to_entity"),
    SERVERBOUND_USE_ITEM_ON(PacketFlow.C2S, "use_item_on"),
    SERVERBOUND_USE_ITEM(PacketFlow.C2S, "use_item"),

    // above is available any version at 1.20+

    // versioned type, align to R version
    // if you really need some shit in small version like 1.21.2 or 1.21.0, please use ALL_C2S or ALL_S2C and use
    // instanceof or sth to check class
    CLIENTBOUND_CHUNK_BATCH_FINISHED(PacketFlow.S2C, "chunk_batch_finished", Version.v1_20_R2),
    CLIENTBOUND_CHUNK_BATCH_START(PacketFlow.S2C, "chunk_batch_start", Version.v1_20_R2),
    SERVERBOUND_CHUNK_BATCH_RECEIVED(PacketFlow.C2S, "chunk_batch_received", Version.v1_20_R2),
    CLIENTBOUND_START_CONFIGURATION(PacketFlow.S2C, "start_configuration", Version.v1_20_R2),
    SERVERBOUND_CONTAINER_SLOT_STATE_CHANGED(PacketFlow.C2S, "container_slot_state_changed", Version.v1_20_R2),
    SERVERBOUND_CONFIGURATION_ACKNOWLEDGED(PacketFlow.C2S, "configuration_acknowledged", Version.v1_20_R2),

    CLIENTBOUND_TICKING_STATE(PacketFlow.S2C, "ticking_state", Version.v1_20_R2),
    CLIENTBOUND_TICKING_STEP(PacketFlow.S2C, "ticking_step", Version.v1_20_R2),
    CLIENTBOUND_RESET_SCORE(PacketFlow.S2C, "reset_score", Version.v1_20_R2),

    SERVERBOUND_DEBUG_SAMPLE_SUBSCRIPTION(PacketFlow.C2S, "debug_sample_subscription", Version.v1_20_R4),
    SERVERBOUND_CHAT_COMMAND(PacketFlow.C2S, "chat_command_unsigned", Version.v1_20_R4),
    CLIENTBOUND_DEBUG_SAMPLE(PacketFlow.S2C, "debug_sample", Version.v1_20_R4),
    CLIENTBOUND_PROJECTILE_POWER(PacketFlow.S2C, "projectile_power", Version.v1_20_R4),

    SERVERBOUND_SELECT_BUNDLE_ITEM(PacketFlow.C2S, "bundle_item_selected", Version.v1_21_R2),
    SERVERBOUND_CLIENT_TICK_END(PacketFlow.C2S, "client_tick_end", Version.v1_21_R2),

    CLIENTBOUND_MOVE_MINECART(PacketFlow.S2C, "move_minecart_along_track", Version.v1_21_R2),
    CLIENTBOUND_ENTITY_POSITION_SYNC(PacketFlow.S2C, "entity_position_sync", Version.v1_21_R2),
    CLIENTBOUND_SET_CURSOR_ITEM(PacketFlow.S2C, "set_cursor_item", Version.v1_21_R2),
    CLIENTBOUND_SET_PLAYER_INVENTORY(PacketFlow.S2C, "set_player_inventory", Version.v1_21_R2),
    CLIENTBOUND_RECIPE_BOOK_ADD(PacketFlow.S2C, "recipe_book_add", Version.v1_21_R2),
    CLIENTBOUND_RECIPE_BOOK_REMOVE(PacketFlow.S2C, "recipe_book_remove", Version.v1_21_R2),
    CLIENTBOUND_RECIPE_BOOK_SETTINGS(PacketFlow.S2C, "recipe_book_settings", Version.v1_21_R2),
    CLIENTBOUND_PLAYER_ROTATION(PacketFlow.S2C, "player_rotation", Version.v1_21_R2),

    ALL_PLAY(null, "all packet", null, null),
    ALL_S2C_PLAY(null, "s2c packet", null, null),
    ALL_C2S_PLAY(null, "s2c packet", null, null),
    UNKNOWN(null, "unknown_packet", null, null),

    // special event channels for our service
    CLIENT_REGISTER(null, "client register at here", null, null),
    CLIENT_UNREGISTER(null, "client register at here", null, null);
    final PacketFlow flow;
    final String name;
    final Version from;
    final Version to;

    @Getter
    final boolean validVersion;

    Class<?> lookup;
    final GamePacketHandler common;

    GamePacket(GamePacketHandler handler, PacketFlow flow, String typeName) {
        this(flow, typeName, null, null, handler);
    }

    GamePacket(PacketFlow flow, String typeName, Version version) {
        this(flow, typeName, version, null, GamePacketHandler.PLAY);
    }

    GamePacket(PacketFlow flow, String typeName) {
        this(flow, typeName, null, null, GamePacketHandler.PLAY);
    }

    GamePacket(PacketFlow flow, String typeName, Version from, Version toNotInclude) {
        this(flow, typeName, from, toNotInclude, GamePacketHandler.PLAY);
    }

    GamePacket(PacketFlow flow, String typeName, Version from, Version toNotInclude, GamePacketHandler common) {
        this.flow = flow;
        this.name = typeName;
        this.from = from;
        this.to = toNotInclude;
        this.common = common;
        boolean valid = true;
        if (this.from != null) {
            valid &= Version.getVersionInstance().isAtLeast(this.from);
        }
        if (this.to != null) {
            valid &= !this.to.isAtLeast(Version.getVersionInstance());
        }
        this.validVersion = valid;

        if (this.validVersion && this.flow != null) {
            this.lookup = lookupClass(name(), common == GamePacketHandler.COMMON);
            if (this.lookup != null) {
                Preconditions.checkArgument(!this.lookup.isInterface());
                ClassMap.CLASS_TO_TYPE.put(this.lookup, this);
            }
        }
    }

    private static Class<?> lookupClass(String name, boolean common) {
        String packetName = convertPacketName(name);
        String fullClassName = "net.minecraft.network.protocol." + (common ? "common" : "game") + "." + packetName;
        try {
            Class<?> clazz = ObfManager.getManager().reobfClass(fullClassName);
            return clazz;
        } catch (Throwable e) {
            try {
                switch (packetName) {
                    case "ClientboundBundleDelimiterPacket":
                        return ObfManager.getManager()
                                .reobfClass("net.minecraft.network.protocol.BundleDelimiterPacket");
                    case "ServerboundBlockEntityTagQueryPacket":
                        return ObfManager.getManager()
                                .reobfClass("net.minecraft.network.protocol.game.ServerboundBlockEntityTagQuery");
                    case "ServerboundEntityTagQueryPacket":
                        return ObfManager.getManager()
                                .reobfClass("net.minecraft.network.protocol.game.ServerboundEntityTagQuery");
                    default:
                        Debug.logger(e, "Could not find class name for", fullClassName, "which from", name);
                }
            } catch (Throwable e1) {
                Debug.logger(e1, "Could not find class name for", fullClassName, "which from", name);
            }
        }
        return null;
    }

    private static String convertPacketName(String name) {
        String name1 = convertName(name);
        if (name1.startsWith("ServerboundMovePlayer")) {
            // deal with inner class;
            String className = "ServerboundMovePlayerPacket$";
            String innerName = name1.substring("ServerboundMovePlayer".length());
            return className + innerName;
        } else if (name1.startsWith("ClientboundMoveEntity")) {
            String className = "ClientboundMoveEntityPacket$";
            String innerName = name1.substring("ClientboundMoveEntity".length());
            return className + innerName;
        } else {
            return name1 + "Packet";
        }
    }

    private static String convertName(String name) {
        String[] words = name.split("_");
        StringBuilder builder = new StringBuilder();
        for (var word : words) {
            if (word.isEmpty()) continue;
            builder.append(Character.toUpperCase(word.charAt(0)));
            builder.append(word.substring(1).toLowerCase(Locale.ROOT));
        }
        return builder.toString();
    }

    public static class ClassMap {
        private static final Map<Class<?>, GamePacket> CLASS_TO_TYPE = new ConcurrentHashMap<>();

        public static Map<Class<?>, GamePacket> getClass2TypeView() {
            return Collections.unmodifiableMap(CLASS_TO_TYPE);
        }

        public static GamePacket getType(Object gamePacket) {
            GamePacket packet = CLASS_TO_TYPE.get(gamePacket.getClass());
            if (packet != null) {
                return packet;
            } else {
                Class wtf = gamePacket.getClass();
                do {
                    packet = CLASS_TO_TYPE.get(wtf);
                    if (packet != null) {
                        CLASS_TO_TYPE.put(gamePacket.getClass(), packet);
                        return packet;
                    }
                } while ((wtf = wtf.getSuperclass()) != null);
                CLASS_TO_TYPE.put(gamePacket.getClass(), UNKNOWN);
                return UNKNOWN;
            }
        }

        static {
            // load outer class to initialize map
            var a = GamePacket.UNKNOWN;
        }

        private static void addOptional(String name, GamePacket type) {
            try {
                Class<?> t = lookupClass(name, type.common == GamePacketHandler.COMMON);
                CLASS_TO_TYPE.put(t, type);
            } catch (Throwable versionedError) {
            }
        }
    }

    public static enum GamePacketHandler {
        COMMON,
        PLAY;
    }
}
