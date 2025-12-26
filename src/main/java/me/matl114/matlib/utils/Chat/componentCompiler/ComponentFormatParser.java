package me.matl114.matlib.utils.chat.componentCompiler;

import static me.matl114.matlib.utils.chat.componentCompiler.ComponentFormatParser.Token.*;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.chars.Char2IntMap;
import it.unimi.dsi.fastutil.chars.Char2IntOpenHashMap;
import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import it.unimi.dsi.fastutil.chars.CharSet;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.matl114.matlib.algorithms.algorithm.CollectionUtils;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.PairList;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.SimpleLinkList;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.Stack;
import me.matl114.matlib.algorithms.dataStructures.frames.initBuidler.InitializeProvider;
import me.matl114.matlib.common.lang.exceptions.CompileError;

public class ComponentFormatParser {
    public static final HashMap<String, ComponentType> TYPE_MATCHER = new HashMap<>() {
        {
            for (ComponentType type : ComponentType.values()) {
                if (!type.isAttach()) put(type.name().toLowerCase(Locale.ROOT) + ":", type);
            }
            // attach_keys;
            put("hover_text:", ComponentType.HOVER);
            put("hover_item:", ComponentType.HOVER);
            put("hover_entity:", ComponentType.HOVER);
            put("click_url:", ComponentType.CLICK);
            put("click_run:", ComponentType.CLICK);
            put("click_suggest:", ComponentType.CLICK);
            put("click_copy:", ComponentType.CLICK);
        }
    };
    public static final FastMatcher TYPE_QUICK_MATCHER =
            new FastMatcher(TYPE_MATCHER.keySet().toArray(String[]::new));
    private static final CharSet COLORS = new CharOpenHashSet(new char[] {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'a', 'B', 'b', 'C', 'c', 'D', 'd', 'E', 'e', 'F', 'f'
    });
    private static final CharSet COLORS_FORMAT = new CharOpenHashSet(new char[] {'&', '§'});

    public static PairList<String, ComponentTokenType> tokenize(
            String input, PairList<String, ComponentTokenType> result) {
        // todo add escape: \ parser
        int strlen = input.length();
        ComponentTokenType currentType = ComponentTokenType.RAW_TEXT;
        StringBuilder building = new StringBuilder();
        int fastMatchType = -999;
        int formatCodeCatcher = -1;
        for (int i = 0; i < strlen; i++) {
            char c = input.charAt(i);
            switch (currentType) {
                case COMPONENT_START:
                case COMPONENT_END:
                    throw new CompileError(
                            CompileError.CompilePeriod.LEXICAL,
                            i,
                            "Unexpected error occurred while tokenizing: currentType = " + currentType
                                    + " not allowed");
            }
            switch (c) {
                case '{':
                    // fixme: fix }} , there shouldn't be Empty TEXT Token here
                    if (!building.isEmpty()) {
                        result.put(building.toString(), currentType);
                    }
                    result.put("{", ComponentTokenType.COMPONENT_START);
                    building = new StringBuilder();
                    currentType = ComponentTokenType.COMPONENT_TYPE;
                    fastMatchType = 0;
                    break;
                case '}':
                    // match }, end currentType matcher
                    // fixme: fix }} , there shouldn't be Empty TEXT Token here
                    if (!building.isEmpty()) {
                        result.put(building.toString(), currentType);
                    }
                    result.put("}", ComponentTokenType.COMPONENT_END);
                    building = new StringBuilder();
                    currentType = ComponentTokenType.RAW_TEXT;
                    break;
                case '&':
                case '§':
                    // if not in FORMATTING MODE: START CATCHING FORMAT CODE,CUT OFF CURRENT MODE
                    if (currentType != ComponentTokenType.FORMAT) {
                        currentType = ComponentTokenType.RAW_TEXT;
                        // fixme: fix }} , there shouldn't be Empty TEXT Token here
                        if (!building.isEmpty()) {
                            result.put(building.toString(), currentType);
                        }
                        building = new StringBuilder();
                        building.append(c);
                        currentType = ComponentTokenType.FORMAT;
                        formatCodeCatcher = 0;
                        break;
                    }
                    // if in formatting mode,pass down
                default:
                    switch (currentType) {
                        case COMPONENT_TYPE:
                            building.append(c);
                            fastMatchType = TYPE_QUICK_MATCHER.checkMatchStatus(fastMatchType, c);
                            if (fastMatchType < 0) {
                                //
                                fastMatchType = -999;
                                // fixme: fix }} , there shouldn't be Empty TEXT Token here

                                result.put(building.toString(), currentType);

                                building = new StringBuilder();
                                currentType = ComponentTokenType.RAW_TEXT;
                            } else if (fastMatchType == 0) {
                                if (c == ':') {
                                    throw new CompileError(
                                            CompileError.CompilePeriod.LEXICAL,
                                            i,
                                            "No such component type: " + building.toString());
                                }
                                fastMatchType = -999;
                                // change to RAW_TEXT;
                                currentType = ComponentTokenType.RAW_TEXT;
                            }
                            break;
                        case FORMAT:
                            if (formatCodeCatcher == 0) {
                                // a new format code start
                                switch (c) {
                                    case '&':
                                    case '§':
                                        // 转义
                                        formatCodeCatcher = -1;
                                        building = new StringBuilder().append(c);
                                        currentType = ComponentTokenType.RAW_TEXT;
                                        break;
                                    case '#':
                                        formatCodeCatcher = 1006;
                                        building.append(c);
                                        break;
                                    case 'x':
                                        formatCodeCatcher = 10012;
                                        building.append(c);
                                        break;
                                    default:
                                        building.append(c);
                                        result.put(building.toString(), currentType);
                                        building = new StringBuilder();
                                        currentType = ComponentTokenType.RAW_TEXT;
                                }

                            } else {
                                if (formatCodeCatcher < 1024) {
                                    --formatCodeCatcher;
                                    if (!COLORS.contains(c)) {
                                        throw new CompileError(
                                                CompileError.CompilePeriod.LEXICAL,
                                                i,
                                                "Illegal Color format " + c + " in &# format");
                                    }
                                    building.append(c);
                                    if (formatCodeCatcher <= 1000) {
                                        result.put(building.toString(), currentType);
                                        building = new StringBuilder();
                                        currentType = ComponentTokenType.RAW_TEXT;
                                    }
                                } else {
                                    --formatCodeCatcher;
                                    boolean shouldColor = formatCodeCatcher % 2 == 0;
                                    if (shouldColor && !COLORS.contains(c)) {
                                        throw new CompileError(
                                                CompileError.CompilePeriod.LEXICAL,
                                                i,
                                                "Illegal Color format " + c + " in &x format");
                                    }
                                    if (!shouldColor && !COLORS_FORMAT.contains(c)) {
                                        throw new CompileError(
                                                CompileError.CompilePeriod.LEXICAL,
                                                i,
                                                "Illegal char " + c + " in &x format, expect a char '&' or '§'");
                                    }
                                    building.append(c);
                                    if (formatCodeCatcher <= 10000) {
                                        result.put(building.toString(), currentType);
                                        building = new StringBuilder();
                                        currentType = ComponentTokenType.RAW_TEXT;
                                    }
                                }
                            }
                            break;
                            // catching format code
                        case RAW_TEXT:
                            building.append(c);
                            break;
                    }
            }
        }
        if (!building.isEmpty()) {
            result.put(building.toString(), currentType);
        }
        return result;
    }

    protected enum Token {
        // 终结符
        TEXT,
        FORMAT,
        END_OF_ANY,
        CTYPE,
        ATYPE,
        PTTYPE, // for TEXT placeholder
        PFTYPE, // for FORMAT placeholder
        NULL,
        EOF,
        // 非终极符
        S(false),
        COMP_LIST(false),
        COMP_LIST$1(false),
        //        COMP_LIST$2(false),
        SPECIAL_COMP(false),
        //        APPEND_LIST(false),
        APPEND_LIST$1(false),
        APPEND(false),
        RAW_TEXT_COMP(false),
        RAW_TEXT_LST$1(false),
        RAW_TEXT_LST$2(false),
        //        STATIC_TEXT(false),
        //        STATIC_FORMAT(false),
        FORMAT_LIST(false),
        FORMAT_LIST$1(false),
        TEXT_LIST(false),
        TEXT_LIST$1(false),
        UNIT_TEXT(false),
        UNIT_FORMAT(false),
        ;

        @Getter
        private final boolean terminal;

        Token() {
            this.terminal = true;
        }

        Token(boolean fi) {
            this.terminal = fi;
        }
    }

    private static class Syntax {
        Function<Object[], Object> writeAction;
        Token s;
        Token[] t;

        public String toString() {
            return "Syntax{" + s.name() + "->" + Arrays.asList(t) + "}";
        }

        public Syntax(Function<Object[], Object> write, Token s, Token... t) {
            this.writeAction = write;
            this.s = s;
            this.t = t;
        }
    }

    private static final Function<Object[], Object> NONE = (asts) -> {
        return asts[0];
    };
    // 首先 我们把{去除
    // 我们把{ + nextToken合并为同一个
    // 根据nextToken的数据分布为CTYPE ATYPE FORMAT_PLACEHOLDER TEXT_PLACEHOLDER、
    // 以此解决First集冲突
    // todo s should return a RootComponentAST
    // S -> COMP_LIST APPEND_EVENTS | EOF
    private static final Syntax S11 = new Syntax(
            (obj) -> {
                SimpleLinkList childs = (SimpleLinkList) obj[0];
                MutableComponentAST root = new MutableComponentAST();
                root.children = childs;
                root.optionalEvent = (List<EventAST>) obj[1];
                return root;
            },
            S,
            COMP_LIST,
            APPEND_LIST$1,
            EOF);

    //    private static final Syntax S12 = new Syntax(
    //        NONE,
    //        S,
    //        EOF
    //    );
    // CL ->R C1| S CL | NULL
    // 改写: CL -> R CL1 | S CL
    // CL1 -> S CL | NULL
    // todo remove ClickEvents in COMP_LIST and move it into Special Comp
    // todo: CL -> RAW_TEXT_LIST CL | SPECIAL_COMP CL | NULL
    // todo COMP_LIST should return a SimpleLinkList
    // C1 -> P CL| C2
    // C2 -> S CL | NULL
    // COMP_LIST(0,1,2) should return TextComponentAST
    // RAW_TEXT_COMP should return TextAST
    // SPECIAL_COMP should return ComponentLikeAST
    // APPEND_LIST should return EventListAST
    private static final Syntax S20 = new Syntax(
            (arg) -> {
                return new SimpleLinkList<>();
            },
            COMP_LIST,
            NULL);
    private static final Syntax S21 = new Syntax(
            (arg) -> {
                ((SimpleLinkList) arg[1]).moveToFirst((SimpleLinkList<MutableComponentAST>) arg[0]);
                return arg[1];
            },
            COMP_LIST,
            RAW_TEXT_COMP,
            COMP_LIST$1);
    private static final Syntax S22 = new Syntax(
            (arg) -> {
                ((SimpleLinkList) arg[1]).addFirst((MutableComponentAST) arg[0]);
                return arg[1];
            },
            COMP_LIST,
            SPECIAL_COMP,
            COMP_LIST);
    private static final Syntax S23 = new Syntax(
            (arg) -> {
                ((SimpleLinkList) arg[1]).addFirst((MutableComponentAST) arg[0]);
                return arg[1];
            },
            COMP_LIST$1,
            SPECIAL_COMP,
            COMP_LIST);
    private static final Syntax S24 = new Syntax(
            (arg) -> {
                return new SimpleLinkList<>();
            },
            COMP_LIST$1,
            NULL);
    //    private static final Syntax S25 = new Syntax(
    //        (arg)->{
    //            ((RootComponentAST)arg[1]).children.moveToFirst((SimpleLinkList<MutableComponentAST>)arg[0]);
    //            return arg[1];
    //        },
    //        COMP_LIST$2,
    //        SPECIAL_COMP,COMP_LIST
    //    );
    //    private static final Syntax S26 = new Syntax(
    //        (arg)->{
    //            return new RootComponentAST() ;
    //        },
    //        COMP_LIST,
    //        NULL
    //    );

    // **************************************************************//
    // TODO: refactor this shit: we should make SPECIAL_COMP return a SimpleLinkList<SubComponentAST>,
    // todo: although they may be only one element in
    // Special COMP
    // todo change SPECIAL COMP -> CTYPE COMP_LIST APPEND0 END_OF_ANY
    // todo the first element of COMP_LIST should be the data of SPECIAL COMP, It will be
    // todo Special COMP should return one or more MutableComponent as SimpleLinkList,
    // todo maybe there are RootComp in the list, who jb knows, it should be done in  MutableComponentAST.resolveSpecial
    private static final Syntax S32 = new Syntax(
            (arg) -> {
                return MutableComponentAST.resolveSpecial(
                        (String) arg[0], (SimpleLinkList<MutableComponentAST>) arg[1], ((List<EventAST>) arg[2]));
            },
            SPECIAL_COMP,
            // bugfix: should have null appendList
            CTYPE,
            COMP_LIST,
            APPEND_LIST$1,
            END_OF_ANY);
    // *************************************************************//
    // appending comp list A nonnull, A1 nullable
    // delete APPEND_LIST?
    //    private static final Syntax S41 = new Syntax(
    //        (arg)->{
    //            ((List<EventAST<?>>)arg[1]).add((EventAST<?>) arg[0]);
    //            return arg[1];
    //        },
    //        APPEND_LIST,
    //        APPEND , APPEND_LIST$1
    //    );
    private static final Syntax S42 = new Syntax(
            (arg) -> {
                ((List<EventAST<?>>) arg[1]).add((EventAST<?>) arg[0]);
                return arg[1];
            },
            APPEND_LIST$1,
            APPEND,
            APPEND_LIST$1);
    // APPEND_LIST -> NULL
    private static final Syntax S43 = new Syntax(
            (arg) -> {
                // sequence do no matter
                return new ArrayList<>();
            },
            APPEND_LIST$1,
            NULL);
    // *****************************************************************//
    // single append comp
    private static final Syntax S51 = new Syntax(
            (arg) -> {
                return EventAST.resolve((String) arg[0], (SimpleLinkList<MutableComponentAST>) arg[1]);
            },
            APPEND,
            ATYPE,
            RAW_TEXT_COMP,
            END_OF_ANY);
    // TODO refactor this shit: we made RAW_TEXT_COMP return a SimpleLinkList<SubComponentAST>
    // ***************************************************************888//
    // RAW_TEXT_LIST R nonnull R1 nullable
    // R -> T R1 | F R2
    // R1 -> F R2 | NULL
    // R2 -> T R1 | NULL
    private static final Syntax S61 = new Syntax(
            (arg) -> {
                SimpleLinkList<MutableComponentAST> list = (SimpleLinkList<MutableComponentAST>) arg[1];
                MutableComponentAST newComp = new MutableComponentAST();
                newComp.conponentContent = (SimpleLinkList<BaseTypeAST>) arg[0];
                list.addFirst(newComp);
                return arg[1];
            },
            RAW_TEXT_COMP,
            TEXT_LIST,
            RAW_TEXT_LST$1);
    // start with & ,
    // it include &a
    // some stupid sb use &a&a&a&a&a with NO FUCKING MESSAGE AFTER THIS
    private static final Syntax S64 = new Syntax(
            (arg) -> {
                SimpleLinkList<MutableComponentAST> list = (SimpleLinkList<MutableComponentAST>) arg[1];
                if (!list.isEmpty()) {
                    MutableComponentAST ast = list.getFirst();
                    ast.styleAst = (SimpleLinkList<BaseTypeAST>) arg[0];
                }
                return arg[1];
            },
            RAW_TEXT_COMP,
            FORMAT_LIST,
            RAW_TEXT_LST$2);
    // <message> (&a<message>)*
    private static final Syntax S66 = new Syntax(
            (arg) -> {
                SimpleLinkList<MutableComponentAST> list = (SimpleLinkList<MutableComponentAST>) arg[1];
                MutableComponentAST newComp = new MutableComponentAST();
                newComp.conponentContent = (SimpleLinkList<BaseTypeAST>) arg[0];
                list.addFirst(newComp);
                return arg[1];
            },
            RAW_TEXT_LST$2,
            TEXT_LIST,
            RAW_TEXT_LST$1);
    // null
    private static final Syntax S67 = new Syntax(
            (arg) -> {
                return new SimpleLinkList<>();
            },
            RAW_TEXT_LST$2,
            NULL);

    // RAW_TEXT_LIST -> (&a<message>)*
    private static final Syntax S63 = new Syntax(
            (arg) -> {
                SimpleLinkList<MutableComponentAST> list = (SimpleLinkList<MutableComponentAST>) arg[1];
                if (!list.isEmpty()) {
                    MutableComponentAST ast = list.getFirst();
                    ast.styleAst = (SimpleLinkList<BaseTypeAST>) arg[0];
                }
                return arg[1];
            },
            RAW_TEXT_LST$1,
            FORMAT_LIST,
            RAW_TEXT_LST$2);

    private static final Syntax S65 = new Syntax(
            (arg) -> {
                return new SimpleLinkList<MutableComponentAST>();
            },
            RAW_TEXT_LST$1,
            NULL);
    // TODO refactor this shit: we made FORMAT LIST AND TEXT LIST return SimpleLinkList<BaseTypeAST>
    // ***********************************************************************************************//
    // F: belike FU list
    // FORMAT_LIST -> NULLABLE FORMAT LIST
    private static final Syntax S81 = new Syntax(
            (arg) -> {
                SimpleLinkList<BaseTypeAST> list = (SimpleLinkList<BaseTypeAST>) arg[1];
                list.addFirst((BaseTypeAST) arg[0]);
                return arg[1];
            },
            FORMAT_LIST,
            UNIT_FORMAT,
            FORMAT_LIST$1);
    // FORMAT_LIST$1 ->  NONNULL FORMAT LIST
    private static final Syntax S82 = new Syntax(
            (arg) -> {
                SimpleLinkList<BaseTypeAST> list = (SimpleLinkList<BaseTypeAST>) arg[1];
                list.addFirst((BaseTypeAST) arg[0]);
                return arg[1];
            },
            FORMAT_LIST$1,
            UNIT_FORMAT,
            FORMAT_LIST$1);
    private static final Syntax S83 = new Syntax(
            (arg) -> {
                return new SimpleLinkList<>();
            },
            FORMAT_LIST$1,
            NULL);
    // ********************************************************************************************//
    // T belike:
    // TEXT_LIST -> NONNULL UNIT LIST
    private static final Syntax S91 = new Syntax(
            (arg) -> {
                SimpleLinkList<BaseTypeAST> list = (SimpleLinkList<BaseTypeAST>) arg[1];
                list.addFirst((BaseTypeAST) arg[0]);
                return arg[1];
            },
            TEXT_LIST,
            UNIT_TEXT,
            TEXT_LIST$1);
    // TEXT_LIST$1 -> NULLABLE UNIT LIST
    private static final Syntax S92 = new Syntax(
            (arg) -> {
                SimpleLinkList<BaseTypeAST> list = (SimpleLinkList<BaseTypeAST>) arg[1];
                list.addFirst((BaseTypeAST) arg[0]);
                return arg[1];
            },
            TEXT_LIST$1,
            UNIT_TEXT,
            TEXT_LIST$1);
    private static final Syntax S93 = new Syntax(
            (arg) -> {
                return new SimpleLinkList<BaseTypeAST>();
            },
            TEXT_LIST$1,
            NULL);
    // *****************************************************************************************//
    // fu and tu belike
    // UNIT -> PLACEHOLDER AND STRING TEXT
    private static final Syntax S95 = new Syntax(
            (arg) -> {
                return BaseTypeAST.ofRawString((String) arg[0]);
            },
            UNIT_TEXT,
            TEXT);
    private static final Syntax S94 = new Syntax(
            (arg) -> {
                return BaseTypeAST.ofPlaceholderString((String) arg[1]);
            },
            UNIT_TEXT,
            PTTYPE,
            TEXT,
            END_OF_ANY);
    private static final Syntax S96 = new Syntax(
            (arg) -> {
                return BaseTypeAST.ofRawFormat((String) arg[0]);
            },
            UNIT_FORMAT,
            FORMAT);
    private static final Syntax S97 = new Syntax(
            (arg) -> {
                return BaseTypeAST.ofPlaceholderString((String) arg[1]);
            },
            UNIT_FORMAT,
            PFTYPE,
            TEXT,
            END_OF_ANY);
    // ******************************************************************************************//

    private static final Token[] tokens = Token.values();
    private static final int inputTokenAmount =
            (int) Arrays.stream(Token.values()).filter(Token::isTerminal).count();
    private static final int unTerminalTokenAmount = Token.values().length - inputTokenAmount;
    private static final int[] inputTokenOrdinal = (int[]) new InitializeProvider(() -> {
                int a = tokens.length;
                int[] raw = new int[a];
                int count = 0;
                for (int i = 0; i < a; i++) {
                    if (tokens[i].isTerminal()) {
                        raw[i] = count;
                        count++;
                    } else {
                        raw[i] = -1;
                    }
                }
                return raw;
            })
            .v();
    private static final int[] unTerminalTokenOrdinal = (int[]) new InitializeProvider(() -> {
                int a = tokens.length;
                int[] raw = new int[a];
                int count = 0;
                for (int i = 0; i < a; i++) {
                    if (!tokens[i].isTerminal()) {
                        raw[i] = count;
                        count++;
                    } else {
                        raw[i] = -1;
                    }
                }
                return raw;
            })
            .v();
    private static final Syntax[] allSyntax = new Syntax[] {
        S11, S20, S21, S22, S23, S24, S32, S42, S43, S51, S61, S63, S64, S65, S66, S67, S81, S82, S83, S91, S92, S93,
        S94, S95, S96, S97
    };
    private static final Map<Token, Set<Syntax>> tokenToSyntax = new HashMap<>() {
        {
            for (Syntax syntax : allSyntax) {
                computeIfAbsent(syntax.s, s -> new HashSet<>()).add(syntax);
            }
        }
    };

    private static final Syntax[][] predictMap = new Syntax[unTerminalTokenAmount + 1][inputTokenAmount + 1];

    static {
        // compute FIRST and FOLLOW
        HashMap<Token, Set<Token>> firstSets = new HashMap<>();
        HashMap<List<Token>, Set<Token>> listTokenfirstSets = new HashMap<>();
        HashMap<Token, Set<Token>> followSets = new HashMap<>();
        boolean modifiedAnyFirst;
        // 终结符
        for (Token t : tokens) {
            if (t.isTerminal()) {
                firstSets.put(t, Set.of(t));
            }
        }
        do {
            modifiedAnyFirst = false;
            for (Syntax s : allSyntax) {
                var originX = firstSets.getOrDefault(s.s, Set.of());
                Set<Token> tokenX = new HashSet<>(originX);
                Token[] list = s.t;
                // X -> NULL
                if (list.length == 1 && list[0] == NULL) {
                    tokenX.add(NULL);
                } else {
                    // X -> Y1Y2Y3...Yk
                    //
                    int eofIndex = 0;
                    for (; eofIndex < list.length; eofIndex++) {
                        // a in First(Y_i) and NULL in First(Y_1,...(i-1))
                        // 加入非EOF 符号
                        var re = firstSets.getOrDefault(list[eofIndex], Set.of());
                        if (!re.contains(NULL)) {
                            tokenX.addAll(re);
                            break;
                        } else {
                            re = new HashSet<>(re);
                            re.remove(NULL);
                            tokenX.addAll(re);
                        }
                    }
                    // if NULL in First(Y_1,...(k)) then NULL in First(X)
                    if (eofIndex == list.length) {
                        tokenX.add(NULL);
                    }
                }
                modifiedAnyFirst |= tokenX.size() != originX.size();
                firstSets.put(s.s, tokenX);
            }
        } while (modifiedAnyFirst);
        Function<List<Token>, Set<Token>> function = list -> {
            Set<Token> tokenS = new HashSet<>();
            int indexOfEOF = 0;
            for (; indexOfEOF < list.size(); indexOfEOF++) {
                // 加入非EOF 符号
                var re = firstSets.getOrDefault(list.get(indexOfEOF), Set.of());
                if (!re.contains(NULL)) {
                    tokenS.addAll(re);
                    break;
                } else {
                    re = new HashSet<>(re);
                    re.remove(NULL);
                    tokenS.addAll(re);
                }
            }
            if (indexOfEOF == list.size()) {
                tokenS.add(NULL);
            }
            return tokenS;
        };
        boolean modifiedAnyFollow;
        followSets.put(S, Set.of(EOF));
        do {
            modifiedAnyFollow = false;
            for (Syntax s : allSyntax) {
                Token[] tt = s.t;
                for (int i = 0; i < tt.length; ++i) {
                    Token B = s.t[i];
                    // no 终结符
                    if (B.isTerminal()) {
                        continue;
                    }
                    Set<Token> originB = followSets.getOrDefault(B, Set.of());
                    Set<Token> followB = new HashSet<>(originB);
                    if (i == tt.length - 1) {
                        Set<Token> followA = followSets.getOrDefault(s.s, Set.of());
                        followB.addAll(followA);
                    } else {
                        Token[] beta = Arrays.copyOfRange(s.t, i + 1, s.t.length);
                        Set<Token> firstBeta =
                                new HashSet<>(listTokenfirstSets.computeIfAbsent(Arrays.asList(beta), function));
                        if (firstBeta.contains(NULL)) {
                            firstBeta.remove(NULL);
                            followB.addAll(firstBeta);
                            Set<Token> followA = followSets.getOrDefault(s.s, Set.of());
                            followB.addAll(followA);
                        } else {
                            followB.addAll(firstBeta);
                        }
                    }
                    boolean update = followB.size() != originB.size();
                    modifiedAnyFollow |= update;
                    if (update && (B == RAW_TEXT_COMP || B == COMP_LIST)) {
                        //  Debug.logger("Update "+ B +" lst at",Arrays.asList(s.t),"value",followB);
                    }

                    followSets.put(B, followB);
                }
            }
        } while (modifiedAnyFollow);
        //        Debug.logger(firstSets);
        //        Debug.logger(followSets);;
        //        Debug.logger(firstSets.get(COMP_LIST$0));
        //        Debug.logger(firstSets.get(COMP_LIST$3));
        //        Debug.logger(followSets.get(COMP_LIST));
        //        Debug.logger(followSets.get(EOF));
        for (var entry : tokenToSyntax.entrySet()) {
            List<Syntax> t = entry.getValue().stream().toList();

            for (int i = 0; i < t.size(); ++i) {
                Token main = t.get(i).s;
                Token[] s1 = t.get(i).t;
                Set<Token> first1 = listTokenfirstSets.computeIfAbsent(Arrays.asList(s1), function);
                for (int j = 0; j < t.size(); ++j) {
                    if (i == j) {
                        continue;
                    }
                    Token[] s2 = t.get(j).t;
                    Set<Token> first2 = listTokenfirstSets.computeIfAbsent(Arrays.asList(s2), function);
                    Preconditions.checkState(
                            CollectionUtils.intersection(first1, first2).isEmpty(),
                            "Error in First-First ,expression: ",
                            main,
                            Arrays.asList(s1),
                            " and ",
                            Arrays.asList(s2));
                    if (first1.contains(NULL)) {
                        //   Debug.logger("check First
                        // Follow:",main,Arrays.asList(s1),Arrays.asList(s2),first2,followSets.get(main));
                        Preconditions.checkState(
                                CollectionUtils.intersection(first2, followSets.get(main))
                                        .isEmpty(),
                                "Error First-Follow ,expression: ",
                                main,
                                Arrays.asList(s1),
                                " and ",
                                Arrays.asList(s2),
                                first2,
                                followSets.get(main));
                    }
                }
            }
        }
        // pass condition check
        // Debug.logger(inputTokenAmount,unTerminalTokenAmount);
        for (Syntax s : allSyntax) {
            int firstIndex = unTerminalTokenOrdinal[s.s.ordinal()];
            Preconditions.checkState(firstIndex >= 0 && firstIndex < unTerminalTokenAmount);
            var re = listTokenfirstSets.computeIfAbsent(Arrays.asList(s.t), function);
            for (Token t : re) {
                if (t.isTerminal()) {
                    int secondIndex = inputTokenOrdinal[t.ordinal()];
                    Preconditions.checkState(secondIndex >= 0 && secondIndex < inputTokenAmount);
                    Preconditions.checkState(
                            predictMap[firstIndex][secondIndex] == null || predictMap[firstIndex][secondIndex] == s);
                    predictMap[firstIndex][secondIndex] = s;
                }
            }
            if (re.contains(NULL)) {
                var followA = followSets.get(s.s);
                for (Token t : followA) {
                    int secondIndex = inputTokenOrdinal[t.ordinal()];
                    Preconditions.checkState(secondIndex >= 0 && secondIndex < inputTokenAmount);
                    Preconditions.checkState(
                            predictMap[firstIndex][secondIndex] == null || predictMap[firstIndex][secondIndex] == s,
                            "conflict in map:",
                            predictMap[firstIndex][secondIndex],
                            s,
                            s.s,
                            t,
                            re,
                            followA);
                    predictMap[firstIndex][secondIndex] = s;
                }
            }
        }
    }

    public static MutableComponentAST compile(String input) {
        BiFunction<String, ComponentTokenType, Token> tokenTransferer =
                new BiFunction<String, ComponentTokenType, Token>() {
                    boolean startWaiting;
                    int count = 0;

                    @Override
                    public Token apply(String string, ComponentTokenType type) {
                        count++;
                        if (startWaiting) {
                            startWaiting = false;
                            switch (type) {
                                case COMPONENT_TYPE:
                                    ComponentType type1 = TYPE_MATCHER.get(string);
                                    if (type1 == null) {
                                        throw new CompileError(
                                                CompileError.CompilePeriod.LEXICAL,
                                                count,
                                                "No such component type:"
                                                        + string.substring(0, Math.max(0, string.length() - 2)));
                                    }
                                    if (type1.isAttach()) {
                                        return ATYPE;
                                    } else if (type1.isPrefix()) {
                                        return PFTYPE;
                                    } else {
                                        return CTYPE;
                                    }
                                case RAW_TEXT:
                                    // take care of PTTYPE!
                                    return PTTYPE;
                                default:
                                    throw new CompileError(
                                            CompileError.CompilePeriod.LEXICAL,
                                            count,
                                            "Unexpected component type value after '{' :" + string);
                            }
                        }
                        switch (type) {
                            case COMPONENT_START:
                                startWaiting = true;
                                return null;
                            case COMPONENT_END:
                                return END_OF_ANY;
                            case RAW_TEXT:
                                return TEXT;
                            case FORMAT:
                                return FORMAT;
                            default:
                                throw new CompileError(
                                        CompileError.CompilePeriod.LEXICAL, count, "Unexpected token in text :" + type);
                        }
                    }
                };
        ForwardCompileContent content = new ForwardCompileContent(S11);

        BiConsumer<String, Token> tokenAcceptor = (str, token) -> {
            if (token == PTTYPE) {
                // fix PTTYPE read error in tokenizer
                content.acceptToken(PTTYPE, "{");
                content.acceptToken(TEXT, str);
            } else {
                content.acceptToken(token, str);
            }
        };

        ForwardCompilingList list = new ForwardCompilingList(tokenTransferer, tokenAcceptor);
        tokenize(input, list);
        content.acceptToken(EOF, "");
        return (MutableComponentAST) content.compileResult;
    }

    private static class ForwardCompileContent {
        boolean hasFinish = false;
        private final Stack<Token> tokenStack = new SimpleLinkList<>();
        private final Stack<Consumer<Object>> calculationTree = new SimpleLinkList<>();
        ComponentAST compileResult = null;
        int acceptedToken = 0;

        public ForwardCompileContent(Syntax sourceSyntax) {
            pushInternal(sourceSyntax, (obj) -> compileResult = (ComponentAST) obj);
        }

        private void pushInternal(Syntax syntax, Consumer<Object> popedRequest) {
            int len = syntax.t.length;
            Object[] requiredArguments = new Object[len];
            for (int i = len - 1; i >= 0; --i) {
                tokenStack.push(syntax.t[i]);
                final int idx = i;
                if (i == len - 1) {
                    calculationTree.push((obj) -> {
                        requiredArguments[idx] = obj;
                        popedRequest.accept(syntax.writeAction.apply(requiredArguments));
                    });
                } else {
                    calculationTree.push((obj) -> {
                        requiredArguments[idx] = obj;
                    });
                }
            }
        }

        private void acceptToken(Token token, String val) {
            // Debug.logger("accept token",token, val);
            acceptedToken++;
            if (hasFinish) {
                throw new CompileError(
                        CompileError.CompilePeriod.SYN_ANALYSIS,
                        acceptedToken,
                        "Unexpected token after compile is done");
            }
            Token top = tokenStack.peek();
            boolean shouldForceStop = token == EOF;
            if (shouldForceStop) {
                // bugfix: when eof,no match
                token = NULL;
            }
            int secondIndex = inputTokenOrdinal[token.ordinal()];
            ;
            do {
                if (top == null) {
                    throw new CompileError(
                            CompileError.CompilePeriod.SYN_ANALYSIS,
                            acceptedToken,
                            "Illegal State for Compiler Stack at runtime");
                }
                // run
                if (top.isTerminal()) {
                    // 处理NULL
                    if (top == NULL) {
                        tokenStack.pop();

                        Consumer<Object> terminalTokenMatcher = calculationTree.pop();
                        terminalTokenMatcher.accept(null);
                    } else if (token == top) {
                        tokenStack.pop();

                        Consumer<Object> terminalTokenMatcher = calculationTree.pop();
                        terminalTokenMatcher.accept(val);
                        // wait for next token
                        return;
                    } else {
                        throw new CompileError(
                                CompileError.CompilePeriod.SYN_ANALYSIS, acceptedToken, "Unmatched token for syntax");
                    }
                } else {
                    int firstIndex = unTerminalTokenOrdinal[top.ordinal()];
                    Syntax predict = predictMap[firstIndex][secondIndex];
                    if (predict != null) {
                        tokenStack.pop();

                        Consumer<Object> calculationNode = calculationTree.pop();
                        pushInternal(predict, calculationNode);
                    } else {
                        throw new CompileError(
                                CompileError.CompilePeriod.SYN_ANALYSIS,
                                acceptedToken,
                                "No matching predict for syntax at top " + top + " and input " + token
                                        + " current token stack: " + tokenStack);
                    }
                }
                top = tokenStack.peek();
            } while (top != EOF);
            // execute finish task
            tokenStack.pop();

            Consumer<Object> terminalTokenMatcher = calculationTree.pop();
            terminalTokenMatcher.accept(null);
            hasFinish = true;
        }
    }

    @AllArgsConstructor
    private static class ForwardCompilingList extends PairList<String, ComponentTokenType> {
        private BiFunction<String, ComponentTokenType, Token> mapToToken;
        private BiConsumer<String, Token> tokenAcceptor;

        @Override
        public void put(String key, ComponentTokenType value) {
            var re = mapToToken.apply(key, value);
            if (re != null) {
                tokenAcceptor.accept(key, re);
            }
        }
    }

    private static class FastMatcher {
        String[] patterns;
        int statusCnt = 0;
        Node[] nodes;

        public FastMatcher(String[] patterns) {
            List<Node> list = new ArrayList<>();
            list.add(new Node());
            Node root = list.get(0);
            int patternLen = patterns.length;
            for (int s = 0; s < patternLen; s++) {
                String pattern = patterns[s];
                Node current = root;
                int len = pattern.length();
                for (int i = 0; i < len; i++) {
                    char c = pattern.charAt(i);
                    if (current.inherit.containsKey(c)) {
                        current = list.get(current.inherit.get(c));
                    } else {
                        int nowLen = list.size();
                        Node nextNode = new Node();
                        nextNode.c = c;
                        ++this.statusCnt;
                        nextNode.status = this.statusCnt;
                        nextNode.result = (i == len - 1) ? s : -1;
                        list.add(nextNode);
                        current.inherit.put(c, nowLen);
                        current = nextNode;
                    }
                }
            }
            this.patterns = patterns;
            this.nodes = list.toArray(Node[]::new);
        }

        // -(a+1) -> get a
        public String getMatchingResult(int a) {
            return this.patterns[-a - 1];
        }

        public int checkMatchStatus(int statsCode, char nextChar) {
            Node node = this.nodes[statsCode];
            if (node.inherit.containsKey(nextChar)) {
                int index = node.inherit.get(nextChar);
                Node nextNode = this.nodes[index];
                if (nextNode.result >= 0) {
                    return -nextNode.status - 1;
                }
                return index;
            } else {
                return 0;
            }
        }

        @ToString
        @NoArgsConstructor
        private class Node {
            char c;
            int status = 0;
            Char2IntMap inherit = new Char2IntOpenHashMap();
            int result = -1;
        }
    }
}
