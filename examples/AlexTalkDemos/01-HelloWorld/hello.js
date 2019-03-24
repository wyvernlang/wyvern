(function(){"use strict";Number.prototype._PLUS_ = function(x) { return this + x; };Number.prototype._HYPHEN_ = function(x) { return this - x; };Number.prototype._TIMES_ = function(x) { return this * x; };Number.prototype._DIVIDE_ = function(x) { return this / x; };Number.prototype._MOD_ = function(x) { return this % x; };Number.prototype._LESSTHAN_ = function(x) { return this < x; };Number.prototype._GREATERTHAN_ = function(x) { return this > x; };Number.prototype._EQUAL__EQUAL_ = function(x) { return this == x; };Number.prototype.negate = function() { return -this; };Number.prototype.floor = function() { return Math.floor(this); };Boolean.prototype.ifTrue = function(t, f) { return this ? t.apply() : f.apply(); };Boolean.prototype._AMPERSAND__AMPERSAND_ = function(x) { return this && x; };Boolean.prototype._VERTICALBAR__VERTICALBAR_ = function(x) { return this || x; };Boolean.prototype._NOT_ = function() { return !this; };String.prototype._LESSTHAN_ = function(x) { return this < x; };String.prototype._GREATERTHAN_ = function(x) { return this > x; };String.prototype._EQUAL__EQUAL_ = function(x) { return this == x; };String.prototype._PLUS_ = function(x) { return this + x; };String.prototype._length = function(x) { return this.length; };String.prototype.equals = function(x) { return this === x; };const FFI_runtime = require(process.env.WYVERN_HOME + "/backend/stdlib/support/runtime");
const FFI_stdio = require(process.env.WYVERN_HOME + "/backend/stdlib/support/stdio");
let __temp_0;{;{ let __temp_3 = function() {
let _this = this; _this._t_Int = _this._t_Int
_this._t_Float = _this._t_Float
_this._t_Unit = function() {};
_this._t_String = _this._t_String
_this._t_Character = _this._t_Character
_this._t_Dyn = function() {};
_this._t_Nothing = function() {};
_this._t_Platform = function() {};
_this._t_Java = function() { let _t_Java = function() {};
_t_Java.prototype = new _this._t_Platform(); return _t_Java;}()
_this._t_Python = function() { let _t_Python = function() {};
_t_Python.prototype = new _this._t_Platform(); return _t_Python;}()
_this._t_JavaScript = function() { let _t_JavaScript = function() {};
_t_JavaScript.prototype = new _this._t_Platform(); return _t_JavaScript;}()
let __temp_1 = function() {
let unitSelf = this; };let __temp_2 = new __temp_1();_this.unit = __temp_2;};let __temp_4 = new __temp_3();let system = __temp_4;;{ let runtime = FFI_runtime;;{ let __temp_11 = function() {
let var_23 = this; };__temp_11.prototype.assertion= function(description,expression) { let var_23 = this;let __temp_7 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_7.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;let __temp_5 = function() {
let unitSelf = this; };let __temp_6 = new __temp_5();return __temp_6;}
let __temp_8 = new __temp_7();let __temp_9 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_9.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return runtime.fail(description);}
let __temp_10 = new __temp_9();return (expression?__temp_8.apply():__temp_10.apply());}

__temp_11.prototype.fail= function(description) { let var_23 = this;return runtime.fail(description);}
let __temp_12 = new __temp_11();let var_23 = __temp_12;; let __temp_13 = function() {
let var_24 = this; };__temp_13.prototype.assertion= function(description,expression) { let var_24 = this;return var_23.assertion(description,expression);}

__temp_13.prototype.fail= function(description) { let var_24 = this;return var_23.fail(description);}
let __temp_14 = new __temp_13();__temp_0 = __temp_14;}}}}let MOD_M_wyvern_DOT_runtime = __temp_0;let __temp_15;{;{ let __temp_18 = function() {
let _this = this; _this._t_Int = _this._t_Int
_this._t_Float = _this._t_Float
_this._t_Unit = function() {};
_this._t_String = _this._t_String
_this._t_Character = _this._t_Character
_this._t_Dyn = function() {};
_this._t_Nothing = function() {};
_this._t_Platform = function() {};
_this._t_Java = function() { let _t_Java = function() {};
_t_Java.prototype = new _this._t_Platform(); return _t_Java;}()
_this._t_Python = function() { let _t_Python = function() {};
_t_Python.prototype = new _this._t_Platform(); return _t_Python;}()
_this._t_JavaScript = function() { let _t_JavaScript = function() {};
_t_JavaScript.prototype = new _this._t_Platform(); return _t_JavaScript;}()
let __temp_16 = function() {
let unitSelf = this; };let __temp_17 = new __temp_16();_this.unit = __temp_17;};let __temp_19 = new __temp_18();let system = __temp_19;;{ let __temp_30 = function() {
let var_2 = this; var_2._t_Option = function() {};
var_2._t_Some = function() { let _t_Some = function() {};
_t_Some.prototype = new var_2._t_Option(); return _t_Some;}()
var_2._t_None = function() { let _t_None = function() {};
_t_None.prototype = new var_2._t_Option(); return _t_None;}()};__temp_30.prototype.Some= function(__generic__T,x) { let var_2 = this;let __temp_22 = function() {
let _this = this; _this._t_T = __generic__T._t_T
_this.content = x;
_this.value = x;
_this.isDefined = true;};__temp_22.prototype = new var_2._t_Some();__temp_22.prototype.map= function(__generic__U,f) { let _this = this;let __temp_20 = function() {
let dontcare = this; dontcare._t_T = __generic__U._t_U};let __temp_21 = new __temp_20();return var_2.Some(__temp_21,f.apply(x));}

__temp_22.prototype.flatMap= function(__generic__U,f) { let _this = this;return f.apply(x);}

__temp_22.prototype.getOrElse= function(defaultValue) { let _this = this;return x;}

__temp_22.prototype.orElse= function(x) { let _this = this;return _this;}
let __temp_23 = new __temp_22();return __temp_23;}

__temp_30.prototype.None= function(__generic__T) { let var_2 = this;let __temp_28 = function() {
let _this = this; _this._t_T = __generic__T._t_T
_this.isDefined = false;};__temp_28.prototype = new var_2._t_None();__temp_28.prototype.map= function(__generic__U,f) { let _this = this;let __temp_24 = function() {
let dontcare = this; dontcare._t_T = __generic__U._t_U};let __temp_25 = new __temp_24();return var_2.None(__temp_25);}

__temp_28.prototype.flatMap= function(__generic__U,f) { let _this = this;let __temp_26 = function() {
let dontcare = this; dontcare._t_T = __generic__U._t_U};let __temp_27 = new __temp_26();return var_2.None(__temp_27);}

__temp_28.prototype.getOrElse= function(defaultValue) { let _this = this;return defaultValue.apply();}

__temp_28.prototype.orElse= function(x) { let _this = this;return x.apply();}
let __temp_29 = new __temp_28();return __temp_29;}
let __temp_31 = new __temp_30();let var_2 = __temp_31;; let __temp_32 = function() {
let var_3 = this; var_3._t_Option = var_2._t_Option
var_3._t_Some = var_2._t_Some
var_3._t_None = var_2._t_None};__temp_32.prototype.Some= function(__generic__T,x) { let var_3 = this;return var_2.Some(__generic__T,x);}

__temp_32.prototype.None= function(__generic__T) { let var_3 = this;return var_2.None(__generic__T);}
let __temp_33 = new __temp_32();__temp_15 = __temp_33;}}}let MOD_M_wyvern_DOT_option = __temp_15;let __temp_34;{;{ let __temp_37 = function() {
let _this = this; _this._t_Int = _this._t_Int
_this._t_Float = _this._t_Float
_this._t_Unit = function() {};
_this._t_String = _this._t_String
_this._t_Character = _this._t_Character
_this._t_Dyn = function() {};
_this._t_Nothing = function() {};
_this._t_Platform = function() {};
_this._t_Java = function() { let _t_Java = function() {};
_t_Java.prototype = new _this._t_Platform(); return _t_Java;}()
_this._t_Python = function() { let _t_Python = function() {};
_t_Python.prototype = new _this._t_Platform(); return _t_Python;}()
_this._t_JavaScript = function() { let _t_JavaScript = function() {};
_t_JavaScript.prototype = new _this._t_Platform(); return _t_JavaScript;}()
let __temp_35 = function() {
let unitSelf = this; };let __temp_36 = new __temp_35();_this.unit = __temp_36;};let __temp_38 = new __temp_37();let system = __temp_38;;{ let option = MOD_M_wyvern_DOT_option;;{ let __temp_39 = function() {
let unitSelf = this; };let __temp_40 = new __temp_39();let utils = __temp_40;;{ let __temp_45 = function() {
let var_4 = this; var_4._t_Regex = function() {};
var_4._t_Match = function() {};};__temp_45.prototype.makeMatch= function(m,a) { let var_4 = this;let __temp_41 = function() {
let _this = this; };__temp_41.prototype.matched= function() { let _this = this;return m;}

__temp_41.prototype.after= function() { let _this = this;return a;}
let __temp_42 = new __temp_41();return __temp_42;}

__temp_45.prototype.apply= function(regex) { let var_4 = this;let __temp_43 = function() {
let _this = this; };__temp_43.prototype.findPrefixOf= function(source) { let _this = this;return utils.findPrefixOf(regex,source);}

__temp_43.prototype.findPrefixMatchOf= function(source) { let _this = this;return utils.findPrefixMatchOf(regex,source);}
let __temp_44 = new __temp_43();return __temp_44;}
let __temp_46 = new __temp_45();let var_4 = __temp_46;; let __temp_47 = function() {
let var_5 = this; var_5._t_Regex = var_4._t_Regex
var_5._t_Match = var_4._t_Match};__temp_47.prototype.makeMatch= function(m,a) { let var_5 = this;return var_4.makeMatch(m,a);}

__temp_47.prototype.apply= function(regex) { let var_5 = this;return var_4.apply(regex);}
let __temp_48 = new __temp_47();__temp_34 = __temp_48;}}}}}let MOD_M_wyvern_DOT_util_DOT_matching_DOT_regex = __temp_34;let __temp_49;{;{ let __temp_52 = function() {
let _this = this; _this._t_Int = _this._t_Int
_this._t_Float = _this._t_Float
_this._t_Unit = function() {};
_this._t_String = _this._t_String
_this._t_Character = _this._t_Character
_this._t_Dyn = function() {};
_this._t_Nothing = function() {};
_this._t_Platform = function() {};
_this._t_Java = function() { let _t_Java = function() {};
_t_Java.prototype = new _this._t_Platform(); return _t_Java;}()
_this._t_Python = function() { let _t_Python = function() {};
_t_Python.prototype = new _this._t_Platform(); return _t_Python;}()
_this._t_JavaScript = function() { let _t_JavaScript = function() {};
_t_JavaScript.prototype = new _this._t_Platform(); return _t_JavaScript;}()
let __temp_50 = function() {
let unitSelf = this; };let __temp_51 = new __temp_50();_this.unit = __temp_51;};let __temp_53 = new __temp_52();let system = __temp_53;;{ let option = MOD_M_wyvern_DOT_option;;{ let regex = MOD_M_wyvern_DOT_util_DOT_matching_DOT_regex;;{ let __temp_54 = function() {
let var_6 = this; var_6._t_Option = MOD_M_wyvern_DOT_option._t_Option};let __temp_55 = new __temp_54();let var_6 = __temp_55;;{ let __temp_187 = function() {
let var_7 = this; var_7._t_List = function() {};};__temp_187.prototype.make= function(__generic__E) { let var_7 = this;let __temp_56;{;{ let __temp_96 = function() {
let var_8 = this; var_8._t_Cell = function() {};};__temp_96.prototype.makeCell= function(e,n) { let var_8 = this;let __temp_92 = function() {
let self = this; self.element = e;
self.next = n;};__temp_92.prototype._getElement= function() { let self = this;return (self).element;}

__temp_92.prototype._setElement= function(x) { let self = this;return (self).element = x;}

__temp_92.prototype._getNext= function() { let self = this;return (self).next;}

__temp_92.prototype._setNext= function(x) { let self = this;return (self).next = x;}

__temp_92.prototype.find= function(pred) { let self = this;let __temp_59 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_59.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;let __temp_57 = function() {
let dontcare = this; dontcare._t_T = __generic__E._t_E};let __temp_58 = new __temp_57();return MOD_M_wyvern_DOT_option.Some(__temp_58,(self).element);}
let __temp_60 = new __temp_59();let __temp_65 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_65.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;let __temp_61 = function() {
let dontcare = this; dontcare._t_U = __generic__E._t_E};let __temp_62 = new __temp_61();let __temp_63 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_63.prototype.apply= function(c) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return c.find(pred);}
let __temp_64 = new __temp_63();return (self).next.flatMap(__temp_62,__temp_64);}
let __temp_66 = new __temp_65();return (pred.apply((self).element)?__temp_60.apply():__temp_66.apply());}

__temp_92.prototype.get= function(n) { let self = this;let __temp_69 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_69.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;let __temp_67 = function() {
let dontcare = this; dontcare._t_T = __generic__E._t_E};let __temp_68 = new __temp_67();return MOD_M_wyvern_DOT_option.Some(__temp_68,(self).element);}
let __temp_70 = new __temp_69();let __temp_75 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_75.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;let __temp_71 = function() {
let dontcare = this; dontcare._t_U = __generic__E._t_E};let __temp_72 = new __temp_71();let __temp_73 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_73.prototype.apply= function(c) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return c.get((n) - (1));}
let __temp_74 = new __temp_73();return (self).next.flatMap(__temp_72,__temp_74);}
let __temp_76 = new __temp_75();return ((n) == (0)?__temp_70.apply():__temp_76.apply());}

__temp_92.prototype.getCell= function(n) { let self = this;let __temp_79 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_79.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;let __temp_77 = function() {
let dontcare = this; dontcare._t_T = var_8._t_Cell};let __temp_78 = new __temp_77();return MOD_M_wyvern_DOT_option.Some(__temp_78,self);}
let __temp_80 = new __temp_79();let __temp_85 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_85.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;let __temp_81 = function() {
let dontcare = this; dontcare._t_U = var_8._t_Cell};let __temp_82 = new __temp_81();let __temp_83 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_83.prototype.apply= function(c) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return c.getCell((n) - (1));}
let __temp_84 = new __temp_83();return (self).next.flatMap(__temp_82,__temp_84);}
let __temp_86 = new __temp_85();return ((n) == (0)?__temp_80.apply():__temp_86.apply());}

__temp_92.prototype.do= function(f) { let self = this;let __temp_87;{;f.apply((self).element); let __temp_88 = function() {
let dontcare = this; dontcare._t_U = function() {};};let __temp_89 = new __temp_88();let __temp_90 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_90.prototype.apply= function(c) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return c.do(f);}
let __temp_91 = new __temp_90();__temp_87 = (self).next.map(__temp_89,__temp_91);}return __temp_87;}
let __temp_93 = new __temp_92();return __temp_93;}

__temp_96.prototype.makeOneCell= function(e) { let var_8 = this;let __temp_94 = function() {
let dontcare = this; dontcare._t_T = var_8._t_Cell};let __temp_95 = new __temp_94();return var_8.makeCell(e,MOD_M_wyvern_DOT_option.None(__temp_95));}
let __temp_97 = new __temp_96();let var_8 = __temp_97;; let __temp_183 = function() {
let self = this; self._t_E = __generic__E._t_E
let __temp_98 = function() {
let dontcare = this; dontcare._t_T = var_8._t_Cell};let __temp_99 = new __temp_98();self.first = MOD_M_wyvern_DOT_option.None(__temp_99);
let __temp_100 = function() {
let dontcare = this; dontcare._t_T = var_8._t_Cell};let __temp_101 = new __temp_100();self.last = MOD_M_wyvern_DOT_option.None(__temp_101);
self.size = 0;};__temp_183.prototype._getFirst= function() { let self = this;return (self).first;}

__temp_183.prototype._setFirst= function(x) { let self = this;return (self).first = x;}

__temp_183.prototype._getLast= function() { let self = this;return (self).last;}

__temp_183.prototype._setLast= function(x) { let self = this;return (self).last = x;}

__temp_183.prototype._getSize= function() { let self = this;return (self).size;}

__temp_183.prototype._setSize= function(x) { let self = this;return (self).size = x;}

__temp_183.prototype.append= function(e) { let self = this;let __temp_102;{;{ let __temp_115 = function() {
let var_9 = this; };__temp_115.prototype.thenCase= function() { let var_9 = this;let __temp_103;{;{ let cell = var_8.makeOneCell(e);;let __temp_104 = function() {
let dontcare = this; dontcare._t_U = function() {};};let __temp_105 = new __temp_104();let __temp_108 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_108.prototype.apply= function(c) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;let __temp_106 = function() {
let dontcare = this; dontcare._t_T = var_8._t_Cell};let __temp_107 = new __temp_106();return (c).next = MOD_M_wyvern_DOT_option.Some(__temp_107,cell);}
let __temp_109 = new __temp_108();(self).last.map(__temp_105,__temp_109); let __temp_110 = function() {
let dontcare = this; dontcare._t_T = var_8._t_Cell};let __temp_111 = new __temp_110();__temp_103 = (self).last = MOD_M_wyvern_DOT_option.Some(__temp_111,cell);}}return __temp_103;}

__temp_115.prototype.elseCase= function() { let var_9 = this;let __temp_112;{;let __temp_113 = function() {
let dontcare = this; dontcare._t_T = var_8._t_Cell};let __temp_114 = new __temp_113();(self).first = MOD_M_wyvern_DOT_option.Some(__temp_114,var_8.makeOneCell(e)); __temp_112 = (self).last = (self).first;}return __temp_112;}
let __temp_116 = new __temp_115();let var_9 = __temp_116;;let __temp_117 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_117.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return var_9.thenCase();}
let __temp_118 = new __temp_117();let __temp_119 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_119.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return var_9.elseCase();}
let __temp_120 = new __temp_119();(((self).first).isDefined?__temp_118.apply():__temp_120.apply()); __temp_102 = (self).size = ((self).size) + (1);}}return __temp_102;}

__temp_183.prototype.appendAll= function(other) { let self = this;let __temp_121 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_121.prototype.apply= function(e) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return self.append(e);}
let __temp_122 = new __temp_121();return other.do(__temp_122);}

__temp_183.prototype.find= function(pred) { let self = this;let __temp_123 = function() {
let dontcare = this; dontcare._t_U = __generic__E._t_E};let __temp_124 = new __temp_123();let __temp_125 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_125.prototype.apply= function(c) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return c.find(pred);}
let __temp_126 = new __temp_125();return (self).first.flatMap(__temp_124,__temp_126);}

__temp_183.prototype._length= function() { let self = this;return (self).size;}

__temp_183.prototype.get= function(n) { let self = this;let __temp_127 = function() {
let dontcare = this; dontcare._t_U = __generic__E._t_E};let __temp_128 = new __temp_127();let __temp_129 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_129.prototype.apply= function(c) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return c.get(n);}
let __temp_130 = new __temp_129();return (self).first.flatMap(__temp_128,__temp_130);}

__temp_183.prototype.remove= function(n) { let self = this;let __temp_131;{;{ let __temp_168 = function() {
let var_10 = this; };__temp_168.prototype.definitelyRemove= function() { let var_10 = this;let __temp_132;{;{ let __temp_135 = function() {
let _this = this; let __temp_133 = function() {
let dontcare = this; dontcare._t_T = var_8._t_Cell};let __temp_134 = new __temp_133();_this.lastCell = MOD_M_wyvern_DOT_option.None(__temp_134);};__temp_135.prototype._getLastCell= function() { let _this = this;return (_this).lastCell;}

__temp_135.prototype._setLastCell= function(x) { let _this = this;return (_this).lastCell = x;}
let __temp_136 = new __temp_135();let _tempLastCell = __temp_136;;{ let __temp_137 = function() {
let var_13 = this; };__temp_137.prototype._getLastCell= function() { let var_13 = this;return (_tempLastCell).lastCell;}

__temp_137.prototype._setLastCell= function(x) { let var_13 = this;return (_tempLastCell).lastCell = x;}
let __temp_138 = new __temp_137();let var_13 = __temp_138;;{ let __temp_152 = function() {
let var_14 = this; };__temp_152.prototype.removeInMiddle= function() { let var_14 = this;let __temp_139;{;{ let __temp_140 = function() {
let dontcare = this; dontcare._t_U = var_8._t_Cell};let __temp_141 = new __temp_140();let __temp_142 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_142.prototype.apply= function(c) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return c.getCell((n) - (1));}
let __temp_143 = new __temp_142();let cellBefore = (self).first.flatMap(__temp_141,__temp_143);;let __temp_144 = function() {
let dontcare = this; dontcare._t_U = function() {};};let __temp_145 = new __temp_144();let __temp_150 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_150.prototype.apply= function(c) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;let __temp_146 = function() {
let dontcare = this; dontcare._t_U = var_8._t_Cell};let __temp_147 = new __temp_146();let __temp_148 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_148.prototype.apply= function(c2) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return (c2).next;}
let __temp_149 = new __temp_148();return (c).next = (c).next.flatMap(__temp_147,__temp_149);}
let __temp_151 = new __temp_150();cellBefore.map(__temp_145,__temp_151); __temp_139 = var_13._setLastCell(cellBefore);}}return __temp_139;}
let __temp_153 = new __temp_152();let var_14 = __temp_153;;let __temp_158 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_158.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;let __temp_154 = function() {
let dontcare = this; dontcare._t_U = var_8._t_Cell};let __temp_155 = new __temp_154();let __temp_156 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_156.prototype.apply= function(c) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return (c).next;}
let __temp_157 = new __temp_156();return (self).first = (self).first.flatMap(__temp_155,__temp_157);}
let __temp_159 = new __temp_158();let __temp_160 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_160.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return var_14.removeInMiddle();}
let __temp_161 = new __temp_160();((n) == (0)?__temp_159.apply():__temp_161.apply());let __temp_162 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_162.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return (self).last = var_13._getLastCell();}
let __temp_163 = new __temp_162();let __temp_166 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_166.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;let __temp_164 = function() {
let unitSelf = this; };let __temp_165 = new __temp_164();return __temp_165;}
let __temp_167 = new __temp_166();((n) == (((self).size) - (1))?__temp_163.apply():__temp_167.apply());(self).size = ((self).size) - (1); __temp_132 = true;}}}}return __temp_132;}
let __temp_169 = new __temp_168();let var_10 = __temp_169;; let __temp_170 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_170.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return false;}
let __temp_171 = new __temp_170();let __temp_172 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_172.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return var_10.definitelyRemove();}
let __temp_173 = new __temp_172();__temp_131 = (((n) > (((self).size) - (1))) || ((n) < (0))?__temp_171.apply():__temp_173.apply());}}return __temp_131;}

__temp_183.prototype.do= function(f) { let self = this;let __temp_174 = function() {
let dontcare = this; dontcare._t_U = function() {};};let __temp_175 = new __temp_174();let __temp_176 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_176.prototype.apply= function(c) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return c.do(f);}
let __temp_177 = new __temp_176();return (self).first.map(__temp_175,__temp_177);}

__temp_183.prototype.map= function(__generic__F,f) { let self = this;let __temp_178;{;{ let __temp_179 = function() {
let dontcare = this; dontcare._t_E = __generic__F._t_F};let __temp_180 = new __temp_179();let newList = var_7.make(__temp_180);;let __temp_181 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_181.prototype.apply= function(e) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return newList.append(f.apply(e));}
let __temp_182 = new __temp_181();self.do(__temp_182); __temp_178 = newList;}}return __temp_178;}
let __temp_184 = new __temp_183();__temp_56 = __temp_184;}}return __temp_56;}

__temp_187.prototype.makeD= function() { let var_7 = this;let __temp_185 = function() {
let dontcare = this; dontcare._t_E = function() {};};let __temp_186 = new __temp_185();return var_7.make(__temp_186);}
let __temp_188 = new __temp_187();let var_7 = __temp_188;; let __temp_189 = function() {
let var_15 = this; var_15._t_Option = var_6._t_Option
var_15._t_List = var_7._t_List};__temp_189.prototype.make= function(__generic__E) { let var_15 = this;return var_7.make(__generic__E);}

__temp_189.prototype.makeD= function() { let var_15 = this;return var_7.makeD();}
let __temp_190 = new __temp_189();__temp_49 = __temp_190;}}}}}}let MOD_M_wyvern_DOT_internal_DOT_list = __temp_49;let __temp_191;{;{ let __temp_194 = function() {
let _this = this; _this._t_Int = _this._t_Int
_this._t_Float = _this._t_Float
_this._t_Unit = function() {};
_this._t_String = _this._t_String
_this._t_Character = _this._t_Character
_this._t_Dyn = function() {};
_this._t_Nothing = function() {};
_this._t_Platform = function() {};
_this._t_Java = function() { let _t_Java = function() {};
_t_Java.prototype = new _this._t_Platform(); return _t_Java;}()
_this._t_Python = function() { let _t_Python = function() {};
_t_Python.prototype = new _this._t_Platform(); return _t_Python;}()
_this._t_JavaScript = function() { let _t_JavaScript = function() {};
_t_JavaScript.prototype = new _this._t_Platform(); return _t_JavaScript;}()
let __temp_192 = function() {
let unitSelf = this; };let __temp_193 = new __temp_192();_this.unit = __temp_193;};let __temp_195 = new __temp_194();let system = __temp_195;;{ let list = MOD_M_wyvern_DOT_internal_DOT_list;;{ let __temp_196 = function() {
let unitSelf = this; };let __temp_197 = new __temp_196();let utils = __temp_197;;{ let __temp_268 = function() {
let var_16 = this; var_16._t_AST = function() {};
var_16._t_Decl = function() {};
var_16._t_Type = function() {};
var_16._t_DeclType = function() {};
var_16._t_VarBinding = function() {};
var_16._t_Case = function() {};
var_16._t_FormalArg = function() {};};__temp_268.prototype.varBinding= function(varName,varType,toReplace) { let var_16 = this;let __temp_198 = function() {
let _this = this; _this.binding = utils.varBinding(varName,varType,toReplace);};let __temp_199 = new __temp_198();return __temp_199;}

__temp_268.prototype.makeCase= function(varName,pattern,body) { let var_16 = this;let __temp_200 = function() {
let _this = this; _this.caseValue = utils.makeCase(varName,pattern,body);};let __temp_201 = new __temp_200();return __temp_201;}

__temp_268.prototype.formalArg= function(name,argType) { let var_16 = this;let __temp_202 = function() {
let _this = this; _this.formalArg = utils.formalArg(name,argType);};let __temp_203 = new __temp_202();return __temp_203;}

__temp_268.prototype.bind= function(bindings,inExpr) { let var_16 = this;let __temp_204 = function() {
let _this = this; _this.ast = utils.bind(bindings,inExpr);};let __temp_205 = new __temp_204();return __temp_205;}

__temp_268.prototype.object= function(decls) { let var_16 = this;let __temp_206 = function() {
let _this = this; _this.ast = utils.object(decls);};let __temp_207 = new __temp_206();return __temp_207;}

__temp_268.prototype.defDeclaration= function(name,formalArgs,returnType,body) { let var_16 = this;let __temp_208 = function() {
let _this = this; _this.decl = utils.defDeclaration(name,formalArgs,returnType,body);};let __temp_209 = new __temp_208();return __temp_209;}

__temp_268.prototype.delegateDeclaration= function(delegateType,fieldName) { let var_16 = this;let __temp_210 = function() {
let _this = this; _this.decl = utils.delegateDeclaration(delegateType,fieldName);};let __temp_211 = new __temp_210();return __temp_211;}

__temp_268.prototype.moduleDeclaration= function(name,formalArgs,moduleType,body,dependencies) { let var_16 = this;let __temp_212 = function() {
let _this = this; _this.decl = utils.moduleDeclaration(name,formalArgs,moduleType,body,dependencies);};let __temp_213 = new __temp_212();return __temp_213;}

__temp_268.prototype.typeDeclaration= function(typeName,sourceType) { let var_16 = this;let __temp_214 = function() {
let _this = this; _this.decl = utils.typeDeclaration(typeName,sourceType);};let __temp_215 = new __temp_214();return __temp_215;}

__temp_268.prototype.valDeclaration= function(fieldName,fieldType,value) { let var_16 = this;let __temp_216 = function() {
let _this = this; _this.decl = utils.valDeclaration(fieldName,fieldType,value);};let __temp_217 = new __temp_216();return __temp_217;}

__temp_268.prototype.varDeclaration= function(fieldName,fieldType,value) { let var_16 = this;let __temp_218 = function() {
let _this = this; _this.decl = utils.varDeclaration(fieldName,fieldType,value);};let __temp_219 = new __temp_218();return __temp_219;}

__temp_268.prototype.int= function(i) { let var_16 = this;let __temp_220 = function() {
let _this = this; _this.ast = utils.intLiteral(i);};let __temp_221 = new __temp_220();return __temp_221;}

__temp_268.prototype.boolean= function(b) { let var_16 = this;let __temp_222 = function() {
let _this = this; _this.ast = utils.booleanLiteral(b);};let __temp_223 = new __temp_222();return __temp_223;}

__temp_268.prototype.string= function(s) { let var_16 = this;let __temp_224 = function() {
let _this = this; _this.ast = utils.stringLiteral(s);};let __temp_225 = new __temp_224();return __temp_225;}

__temp_268.prototype.variable= function(s) { let var_16 = this;let __temp_226 = function() {
let _this = this; _this.ast = utils.variable(s);};let __temp_227 = new __temp_226();return __temp_227;}

__temp_268.prototype.call= function(receiver,methodName,_arguments) { let var_16 = this;let __temp_228 = function() {
let _this = this; _this.ast = utils.methodCall(receiver,methodName,_arguments);};let __temp_229 = new __temp_228();return __temp_229;}

__temp_268.prototype.cast= function(toCastExpr,exprType) { let var_16 = this;let __temp_230 = function() {
let _this = this; _this.ast = utils.cast(toCastExpr,exprType);};let __temp_231 = new __temp_230();return __temp_231;}

__temp_268.prototype.ffi= function(importName,importType) { let var_16 = this;let __temp_232 = function() {
let _this = this; _this.ast = utils.ffi(importName,importType);};let __temp_233 = new __temp_232();return __temp_233;}

__temp_268.prototype.ffiImport= function(ffiType,path,importType) { let var_16 = this;let __temp_234 = function() {
let _this = this; _this.ast = utils.ffiImport(ffiType,path,importType);};let __temp_235 = new __temp_234();return __temp_235;}

__temp_268.prototype.fieldGet= function(objectExpr,fieldName) { let var_16 = this;let __temp_236 = function() {
let _this = this; _this.ast = utils.fieldGet(objectExpr,fieldName);};let __temp_237 = new __temp_236();return __temp_237;}

__temp_268.prototype.fieldSet= function(exprType,object,fieldName,exprToAssign) { let var_16 = this;let __temp_238 = function() {
let _this = this; _this.ast = utils.fieldSet(exprType,object,fieldName,exprToAssign);};let __temp_239 = new __temp_238();return __temp_239;}

__temp_268.prototype.matchExpr= function(matchExpr,elseExpr,cases) { let var_16 = this;let __temp_240 = function() {
let _this = this; _this.ast = utils.matchExpr(matchExpr,elseExpr,cases);};let __temp_241 = new __temp_240();return __temp_241;}

__temp_268.prototype.abstractTypeMember= function(name,isResource) { let var_16 = this;let __temp_242 = function() {
let _this = this; _this.declType = utils.abstractTypeMember(name,isResource);};let __temp_243 = new __temp_242();return __temp_243;}

__temp_268.prototype.concreteTypeMember= function(name,sourceType) { let var_16 = this;let __temp_244 = function() {
let _this = this; _this.declType = utils.concreteTypeMember(name,sourceType);};let __temp_245 = new __temp_244();return __temp_245;}

__temp_268.prototype.defDeclType= function(methodName,returnType,formalArgs) { let var_16 = this;let __temp_246 = function() {
let _this = this; _this.declType = utils.defDeclType(methodName,returnType,formalArgs);};let __temp_247 = new __temp_246();return __temp_247;}

__temp_268.prototype.valDeclType= function(field,valType) { let var_16 = this;let __temp_248 = function() {
let _this = this; _this.declType = utils.valDeclType(field,valType);};let __temp_249 = new __temp_248();return __temp_249;}

__temp_268.prototype.varDeclType= function(field,varType) { let var_16 = this;let __temp_250 = function() {
let _this = this; _this.declType = utils.varDeclType(field,varType);};let __temp_251 = new __temp_250();return __temp_251;}

__temp_268.prototype.parseExpression= function(input,ctx) { let var_16 = this;let __temp_252;{;{ let ctxDyn = ctx;; let __temp_253 = function() {
let _this = this; _this.ast = utils.parseExpression(input,ctxDyn);};let __temp_254 = new __temp_253();__temp_252 = __temp_254;}}return __temp_252;}

__temp_268.prototype.parseExpressionNoContext= function(input) { let var_16 = this;let __temp_255 = function() {
let _this = this; _this.ast = utils.parseExpressionNoContext(input);};let __temp_256 = new __temp_255();return __temp_256;}

__temp_268.prototype.parseGeneratedModule= function(input) { let var_16 = this;let __temp_257 = function() {
let _this = this; _this.ast = utils.parseGeneratedModule(input);};let __temp_258 = new __temp_257();return __temp_258;}

__temp_268.prototype.parseExpressionList= function(input,ctx) { let var_16 = this;let __temp_259;{;{ let ctxDyn = ctx;;{ let __temp_262 = function() {
let var_17 = this; };__temp_262.prototype.javaASTToWyvAST= function(jAST) { let var_17 = this;let __temp_260 = function() {
let _this = this; _this.ast = jAST;};let __temp_261 = new __temp_260();return __temp_261;}
let __temp_263 = new __temp_262();let var_17 = __temp_263;;{ let l = utils.parseExpressionList(input,ctxDyn);; let __temp_264 = function() {
let dontcare = this; dontcare._t_F = var_16._t_AST};let __temp_265 = new __temp_264();let __temp_266 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_266.prototype.apply= function(ast) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return var_17.javaASTToWyvAST(ast);}
let __temp_267 = new __temp_266();__temp_259 = l.map(__temp_265,__temp_267);}}}}return __temp_259;}
let __temp_269 = new __temp_268();let var_16 = __temp_269;;{ let __temp_286 = function() {
let _this = this; };__temp_286.prototype.int= function() { let _this = this;let __temp_270 = function() {
let _this = this; _this.typ = utils.intType();};let __temp_271 = new __temp_270();return __temp_271;}

__temp_286.prototype.dyn= function() { let _this = this;let __temp_272 = function() {
let _this = this; _this.typ = utils.dynType();};let __temp_273 = new __temp_272();return __temp_273;}

__temp_286.prototype.unit= function() { let _this = this;let __temp_274 = function() {
let _this = this; _this.typ = utils.unitType();};let __temp_275 = new __temp_274();return __temp_275;}

__temp_286.prototype.boolean= function() { let _this = this;let __temp_276 = function() {
let _this = this; _this.typ = utils.booleanType();};let __temp_277 = new __temp_276();return __temp_277;}

__temp_286.prototype.string= function() { let _this = this;let __temp_278 = function() {
let _this = this; _this.typ = utils.stringType();};let __temp_279 = new __temp_278();return __temp_279;}

__temp_286.prototype.nominal= function(pathVariable,typeMember) { let _this = this;let __temp_280 = function() {
let _this = this; _this.typ = utils.nominalType(pathVariable,typeMember);};let __temp_281 = new __temp_280();return __temp_281;}

__temp_286.prototype.structural= function(selfName,declTypes) { let _this = this;let __temp_282 = function() {
let _this = this; _this.typ = utils.structuralType(selfName,declTypes);};let __temp_283 = new __temp_282();return __temp_283;}

__temp_286.prototype.refinement= function(typeParams,base) { let _this = this;let __temp_284 = function() {
let _this = this; _this.typ = utils.refinementType(typeParams,base);};let __temp_285 = new __temp_284();return __temp_285;}
let __temp_287 = new __temp_286();let types = __temp_287;;{ let __temp_290 = function() {
let var_18 = this; };__temp_290.prototype.stripLeadingWhitespace= function(input,mustStrip) { let var_18 = this;return utils.stripLeadingWhitespace(input,mustStrip);}

__temp_290.prototype.genIdent= function() { let var_18 = this;return utils.genIdent();}

__temp_290.prototype.let= function(ident,bindingType,bindingValue,inExpr) { let var_18 = this;let __temp_288 = function() {
let _this = this; _this.ast = utils.let(ident,bindingType,bindingValue,inExpr);};let __temp_289 = new __temp_288();return __temp_289;}
let __temp_291 = new __temp_290();let var_18 = __temp_291;; let __temp_292 = function() {
let var_19 = this; var_19._t_AST = var_16._t_AST
var_19._t_Decl = var_16._t_Decl
var_19._t_Type = var_16._t_Type
var_19._t_DeclType = var_16._t_DeclType
var_19._t_VarBinding = var_16._t_VarBinding
var_19._t_Case = var_16._t_Case
var_19._t_FormalArg = var_16._t_FormalArg
var_19.types = types;};__temp_292.prototype.varBinding= function(varName,varType,toReplace) { let var_19 = this;return var_16.varBinding(varName,varType,toReplace);}

__temp_292.prototype.makeCase= function(varName,pattern,body) { let var_19 = this;return var_16.makeCase(varName,pattern,body);}

__temp_292.prototype.formalArg= function(name,argType) { let var_19 = this;return var_16.formalArg(name,argType);}

__temp_292.prototype.bind= function(bindings,inExpr) { let var_19 = this;return var_16.bind(bindings,inExpr);}

__temp_292.prototype.object= function(decls) { let var_19 = this;return var_16.object(decls);}

__temp_292.prototype.defDeclaration= function(name,formalArgs,returnType,body) { let var_19 = this;return var_16.defDeclaration(name,formalArgs,returnType,body);}

__temp_292.prototype.delegateDeclaration= function(delegateType,fieldName) { let var_19 = this;return var_16.delegateDeclaration(delegateType,fieldName);}

__temp_292.prototype.moduleDeclaration= function(name,formalArgs,moduleType,body,dependencies) { let var_19 = this;return var_16.moduleDeclaration(name,formalArgs,moduleType,body,dependencies);}

__temp_292.prototype.typeDeclaration= function(typeName,sourceType) { let var_19 = this;return var_16.typeDeclaration(typeName,sourceType);}

__temp_292.prototype.valDeclaration= function(fieldName,fieldType,value) { let var_19 = this;return var_16.valDeclaration(fieldName,fieldType,value);}

__temp_292.prototype.varDeclaration= function(fieldName,fieldType,value) { let var_19 = this;return var_16.varDeclaration(fieldName,fieldType,value);}

__temp_292.prototype.int= function(i) { let var_19 = this;return var_16.int(i);}

__temp_292.prototype.boolean= function(b) { let var_19 = this;return var_16.boolean(b);}

__temp_292.prototype.string= function(s) { let var_19 = this;return var_16.string(s);}

__temp_292.prototype.variable= function(s) { let var_19 = this;return var_16.variable(s);}

__temp_292.prototype.call= function(receiver,methodName,_arguments) { let var_19 = this;return var_16.call(receiver,methodName,_arguments);}

__temp_292.prototype.cast= function(toCastExpr,exprType) { let var_19 = this;return var_16.cast(toCastExpr,exprType);}

__temp_292.prototype.ffi= function(importName,importType) { let var_19 = this;return var_16.ffi(importName,importType);}

__temp_292.prototype.ffiImport= function(ffiType,path,importType) { let var_19 = this;return var_16.ffiImport(ffiType,path,importType);}

__temp_292.prototype.fieldGet= function(objectExpr,fieldName) { let var_19 = this;return var_16.fieldGet(objectExpr,fieldName);}

__temp_292.prototype.fieldSet= function(exprType,object,fieldName,exprToAssign) { let var_19 = this;return var_16.fieldSet(exprType,object,fieldName,exprToAssign);}

__temp_292.prototype.matchExpr= function(matchExpr,elseExpr,cases) { let var_19 = this;return var_16.matchExpr(matchExpr,elseExpr,cases);}

__temp_292.prototype.abstractTypeMember= function(name,isResource) { let var_19 = this;return var_16.abstractTypeMember(name,isResource);}

__temp_292.prototype.concreteTypeMember= function(name,sourceType) { let var_19 = this;return var_16.concreteTypeMember(name,sourceType);}

__temp_292.prototype.defDeclType= function(methodName,returnType,formalArgs) { let var_19 = this;return var_16.defDeclType(methodName,returnType,formalArgs);}

__temp_292.prototype.valDeclType= function(field,valType) { let var_19 = this;return var_16.valDeclType(field,valType);}

__temp_292.prototype.varDeclType= function(field,varType) { let var_19 = this;return var_16.varDeclType(field,varType);}

__temp_292.prototype.parseExpression= function(input,ctx) { let var_19 = this;return var_16.parseExpression(input,ctx);}

__temp_292.prototype.parseExpressionNoContext= function(input) { let var_19 = this;return var_16.parseExpressionNoContext(input);}

__temp_292.prototype.parseGeneratedModule= function(input) { let var_19 = this;return var_16.parseGeneratedModule(input);}

__temp_292.prototype.parseExpressionList= function(input,ctx) { let var_19 = this;return var_16.parseExpressionList(input,ctx);}

__temp_292.prototype.stripLeadingWhitespace= function(input,mustStrip) { let var_19 = this;return var_18.stripLeadingWhitespace(input,mustStrip);}

__temp_292.prototype.genIdent= function() { let var_19 = this;return var_18.genIdent();}

__temp_292.prototype.let= function(ident,bindingType,bindingValue,inExpr) { let var_19 = this;return var_18.let(ident,bindingType,bindingValue,inExpr);}
let __temp_293 = new __temp_292();__temp_191 = __temp_293;}}}}}}}let MOD_M_wyvern_DOT_internal_DOT_ast = __temp_191;let __temp_294;{;{ let __temp_297 = function() {
let _this = this; _this._t_Int = _this._t_Int
_this._t_Float = _this._t_Float
_this._t_Unit = function() {};
_this._t_String = _this._t_String
_this._t_Character = _this._t_Character
_this._t_Dyn = function() {};
_this._t_Nothing = function() {};
_this._t_Platform = function() {};
_this._t_Java = function() { let _t_Java = function() {};
_t_Java.prototype = new _this._t_Platform(); return _t_Java;}()
_this._t_Python = function() { let _t_Python = function() {};
_t_Python.prototype = new _this._t_Platform(); return _t_Python;}()
_this._t_JavaScript = function() { let _t_JavaScript = function() {};
_t_JavaScript.prototype = new _this._t_Platform(); return _t_JavaScript;}()
let __temp_295 = function() {
let unitSelf = this; };let __temp_296 = new __temp_295();_this.unit = __temp_296;};let __temp_298 = new __temp_297();let system = __temp_298;;{ let __temp_299 = function() {
let unitSelf = this; };let __temp_300 = new __temp_299();let debug = __temp_300;;{ let __temp_301 = function() {
let var_0 = this; };__temp_301.prototype.print= function(text) { let var_0 = this;return debug.print(text);}

__temp_301.prototype.printInt= function(n) { let var_0 = this;return debug.printInt(n);}

__temp_301.prototype.println= function() { let var_0 = this;return debug.println();}
let __temp_302 = new __temp_301();let var_0 = __temp_302;; let __temp_303 = function() {
let var_1 = this; };__temp_303.prototype.print= function(text) { let var_1 = this;return var_0.print(text);}

__temp_303.prototype.printInt= function(n) { let var_1 = this;return var_0.printInt(n);}

__temp_303.prototype.println= function() { let var_1 = this;return var_0.println();}
let __temp_304 = new __temp_303();__temp_294 = __temp_304;}}}}let MOD_M_platform_DOT_java_DOT_debug = __temp_294;let __temp_305;{;{ let __temp_308 = function() {
let _this = this; _this._t_Int = _this._t_Int
_this._t_Float = _this._t_Float
_this._t_Unit = function() {};
_this._t_String = _this._t_String
_this._t_Character = _this._t_Character
_this._t_Dyn = function() {};
_this._t_Nothing = function() {};
_this._t_Platform = function() {};
_this._t_Java = function() { let _t_Java = function() {};
_t_Java.prototype = new _this._t_Platform(); return _t_Java;}()
_this._t_Python = function() { let _t_Python = function() {};
_t_Python.prototype = new _this._t_Platform(); return _t_Python;}()
_this._t_JavaScript = function() { let _t_JavaScript = function() {};
_t_JavaScript.prototype = new _this._t_Platform(); return _t_JavaScript;}()
let __temp_306 = function() {
let unitSelf = this; };let __temp_307 = new __temp_306();_this.unit = __temp_307;};let __temp_309 = new __temp_308();let system = __temp_309;;{ let debug = MOD_M_platform_DOT_java_DOT_debug;;{ let ast = MOD_M_wyvern_DOT_internal_DOT_ast;;{ let option = MOD_M_wyvern_DOT_option;;{ let regex = MOD_M_wyvern_DOT_util_DOT_matching_DOT_regex;;{ let list = MOD_M_wyvern_DOT_internal_DOT_list;;{ let __temp_310 = function() {
let var_20 = this; var_20._t_AST = MOD_M_wyvern_DOT_internal_DOT_ast._t_AST};let __temp_311 = new __temp_310();let var_20 = __temp_311;;{ let __temp_368 = function() {
let var_21 = this; var_21._t_FnExpr = function() {};
var_21._t_Blocks = function() {};};__temp_368.prototype.getIndent= function(x) { let var_21 = this;let __temp_312;{;{ let f = x.substring(0,1);; let __temp_313 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_313.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return (f) + (var_21.getIndent(x.substring(1,(x).length)));}
let __temp_314 = new __temp_313();let __temp_315 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_315.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return "";}
let __temp_316 = new __temp_315();__temp_312 = (((f) == (" ")) || ((f) == ("\t"))?__temp_314.apply():__temp_316.apply());}}return __temp_312;}

__temp_368.prototype.indentHelper= function(x,ind,acc) { let var_21 = this;let __temp_317 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_317.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return acc;}
let __temp_318 = new __temp_317();let __temp_323 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_323.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;let __temp_319 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_319.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return var_21.indentHelper(x.substring(1,(x).length),ind,((acc) + ("\n")) + (ind));}
let __temp_320 = new __temp_319();let __temp_321 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_321.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return var_21.indentHelper(x.substring(1,(x).length),ind,(acc) + (x.substring(0,1)));}
let __temp_322 = new __temp_321();return ((x.substring(0,1)) == ("\n")?__temp_320.apply():__temp_322.apply());}
let __temp_324 = new __temp_323();return (((x).length) == (0)?__temp_318.apply():__temp_324.apply());}

__temp_368.prototype.indent= function(x,ind) { let var_21 = this;return var_21.indentHelper(x,ind,"");}

__temp_368.prototype.getToNewLine= function(x) { let var_21 = this;let __temp_325 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_325.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return "";}
let __temp_326 = new __temp_325();let __temp_331 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_331.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;let __temp_327 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_327.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return "";}
let __temp_328 = new __temp_327();let __temp_329 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_329.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return (x.substring(0,1)) + (var_21.getToNewLine(x.substring(1,(x).length)));}
let __temp_330 = new __temp_329();return ((x.substring(0,1)) == ("\n")?__temp_328.apply():__temp_330.apply());}
let __temp_332 = new __temp_331();return (((x).length) == (0)?__temp_326.apply():__temp_332.apply());}

__temp_368.prototype.indentAndMakeIf= function(x,cond) { let var_21 = this;let __temp_333;{;{ let ind = var_21.getIndent(x);; __temp_333 = (((("if") + (cond)) + ("\n")) + (ind)) + (var_21.indent(x,ind));}}return __temp_333;}

__temp_368.prototype.elifAST= function(input,ctx) { let var_21 = this;let __temp_334;{;{ let elifRegex = MOD_M_wyvern_DOT_util_DOT_matching_DOT_regex.apply("^elif\\s*\\(.*\\)\\s*\n");;{ let mOpt = elifRegex.findPrefixMatchOf(input);;{ let isElif = (mOpt).isDefined;;{ let __temp_335 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_335.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return MOD_M_wyvern_DOT_util_DOT_matching_DOT_regex.makeMatch("",input);}
let __temp_336 = new __temp_335();let em = mOpt.getOrElse(__temp_336).after();;{ let __temp_337 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_337.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return "";}
let __temp_338 = new __temp_337();let __temp_339 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_339.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return var_21.getToNewLine(input.substring(4,(input).length));}
let __temp_340 = new __temp_339();let cond = (((input).length) < (4)?__temp_338.apply():__temp_340.apply());; let __temp_341 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_341.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return var_21.indentAndMakeIf(em,cond);}
let __temp_342 = new __temp_341();let __temp_343 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_343.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return em;}
let __temp_344 = new __temp_343();__temp_334 = (isElif?__temp_342.apply():__temp_344.apply());}}}}}}return __temp_334;}

__temp_368.prototype.elifOrElseAST= function(input,ctx) { let var_21 = this;let __temp_345;{;{ let elseRegex = MOD_M_wyvern_DOT_util_DOT_matching_DOT_regex.apply("^else\\s*\n");;{ let mOpt = elseRegex.findPrefixMatchOf(input);;{ let __temp_346 = function() {
let dontcare = this; dontcare._t_U = function() {};};let __temp_347 = new __temp_346();let __temp_348 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_348.prototype.apply= function(x) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return x.after();}
let __temp_349 = new __temp_348();let __temp_350 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_350.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return var_21.elifAST(input,ctx);}
let __temp_351 = new __temp_350();let body = mOpt.map(__temp_347,__temp_349).getOrElse(__temp_351);; __temp_345 = var_21.toUnitOrAST(body,ctx);}}}}return __temp_345;}

__temp_368.prototype.thenBlockMatch= function(input) { let var_21 = this;let __temp_352;{;{ let blockRegex = MOD_M_wyvern_DOT_util_DOT_matching_DOT_regex.apply("(\\s[^\n]*\n)+");;{ let blockMatchOpt = blockRegex.findPrefixMatchOf(input);;{ let fullMatch = MOD_M_wyvern_DOT_util_DOT_matching_DOT_regex.makeMatch(input,"");;{ let __temp_353 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_353.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return fullMatch;}
let __temp_354 = new __temp_353();let m = blockMatchOpt.getOrElse(__temp_354);; __temp_352 = m;}}}}}return __temp_352;}

__temp_368.prototype.toUnitOrAST= function(input,ctx) { let var_21 = this;let __temp_355;{;{ let stripped = MOD_M_wyvern_DOT_internal_DOT_ast.stripLeadingWhitespace(input,false);; let __temp_356 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_356.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return MOD_M_wyvern_DOT_internal_DOT_ast.int(0);}
let __temp_357 = new __temp_356();let __temp_358 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_358.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return MOD_M_wyvern_DOT_internal_DOT_ast.parseExpression(stripped,ctx);}
let __temp_359 = new __temp_358();__temp_355 = ((input) == ("")?__temp_357.apply():__temp_359.apply());}}return __temp_355;}

__temp_368.prototype.doif= function(condition,tt,ff) { let var_21 = this;let __temp_360 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_360.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return tt.apply();}
let __temp_361 = new __temp_360();let __temp_362 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_362.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return ff.apply();}
let __temp_363 = new __temp_362();return (condition?__temp_361.apply():__temp_363.apply());}

__temp_368.prototype.doifblk= function(condition,block) { let var_21 = this;let __temp_364 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_364.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return block.evalTrue();}
let __temp_365 = new __temp_364();let __temp_366 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_366.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return block.evalFalse();}
let __temp_367 = new __temp_366();return (condition?__temp_365.apply():__temp_367.apply());}
let __temp_369 = new __temp_368();let var_21 = __temp_369;; let __temp_370 = function() {
let var_22 = this; var_22._t_AST = var_20._t_AST
var_22._t_FnExpr = var_21._t_FnExpr
var_22._t_Blocks = var_21._t_Blocks};__temp_370.prototype.getIndent= function(x) { let var_22 = this;return var_21.getIndent(x);}

__temp_370.prototype.indentHelper= function(x,ind,acc) { let var_22 = this;return var_21.indentHelper(x,ind,acc);}

__temp_370.prototype.indent= function(x,ind) { let var_22 = this;return var_21.indent(x,ind);}

__temp_370.prototype.getToNewLine= function(x) { let var_22 = this;return var_21.getToNewLine(x);}

__temp_370.prototype.indentAndMakeIf= function(x,cond) { let var_22 = this;return var_21.indentAndMakeIf(x,cond);}

__temp_370.prototype.elifAST= function(input,ctx) { let var_22 = this;return var_21.elifAST(input,ctx);}

__temp_370.prototype.elifOrElseAST= function(input,ctx) { let var_22 = this;return var_21.elifOrElseAST(input,ctx);}

__temp_370.prototype.thenBlockMatch= function(input) { let var_22 = this;return var_21.thenBlockMatch(input);}

__temp_370.prototype.toUnitOrAST= function(input,ctx) { let var_22 = this;return var_21.toUnitOrAST(input,ctx);}

__temp_370.prototype.doif= function(condition,tt,ff) { let var_22 = this;return var_21.doif(condition,tt,ff);}

__temp_370.prototype.doifblk= function(condition,block) { let var_22 = this;return var_21.doifblk(condition,block);}
let __temp_371 = new __temp_370();__temp_305 = __temp_371;}}}}}}}}}let MOD_M_wyvern_DOT_IfTSL = __temp_305;let __temp_372;{;{ let __temp_375 = function() {
let _this = this; _this._t_Int = _this._t_Int
_this._t_Float = _this._t_Float
_this._t_Unit = function() {};
_this._t_String = _this._t_String
_this._t_Character = _this._t_Character
_this._t_Dyn = function() {};
_this._t_Nothing = function() {};
_this._t_Platform = function() {};
_this._t_Java = function() { let _t_Java = function() {};
_t_Java.prototype = new _this._t_Platform(); return _t_Java;}()
_this._t_Python = function() { let _t_Python = function() {};
_t_Python.prototype = new _this._t_Platform(); return _t_Python;}()
_this._t_JavaScript = function() { let _t_JavaScript = function() {};
_t_JavaScript.prototype = new _this._t_Platform(); return _t_JavaScript;}()
let __temp_373 = function() {
let unitSelf = this; };let __temp_374 = new __temp_373();_this.unit = __temp_374;};let __temp_376 = new __temp_375();let system = __temp_376;;{ let IfTSL = MOD_M_wyvern_DOT_IfTSL;;{ let runtime = MOD_M_wyvern_DOT_runtime;;{ let __temp_377 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_377.prototype.apply= function(cond,tt,ff) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return MOD_M_wyvern_DOT_IfTSL.doif(cond,tt,ff);}
let __temp_378 = new __temp_377();let ifelseARG = __temp_378;;{ let __temp_379 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_379.prototype.apply= function(cond,blocks) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return MOD_M_wyvern_DOT_IfTSL.doifblk(cond,blocks);}
let __temp_380 = new __temp_379();let _if = __temp_380;;let __temp_381 = function() {
let unitSelf = this; };let __temp_382 = new __temp_381();__temp_382; { let __temp_383 = function() {
let var_25 = this; var_25._t_Stdout = function() {};};let __temp_384 = new __temp_383();let var_25 = __temp_384;;}}}}}}}let MOD_M_Stdout = __temp_372;let __temp_385;{;{ let __temp_388 = function() {
let _this = this; _this._t_Int = _this._t_Int
_this._t_Float = _this._t_Float
_this._t_Unit = function() {};
_this._t_String = _this._t_String
_this._t_Character = _this._t_Character
_this._t_Dyn = function() {};
_this._t_Nothing = function() {};
_this._t_Platform = function() {};
_this._t_Java = function() { let _t_Java = function() {};
_t_Java.prototype = new _this._t_Platform(); return _t_Java;}()
_this._t_Python = function() { let _t_Python = function() {};
_t_Python.prototype = new _this._t_Platform(); return _t_Python;}()
_this._t_JavaScript = function() { let _t_JavaScript = function() {};
_t_JavaScript.prototype = new _this._t_Platform(); return _t_JavaScript;}()
let __temp_386 = function() {
let unitSelf = this; };let __temp_387 = new __temp_386();_this.unit = __temp_387;};let __temp_389 = new __temp_388();let system = __temp_389;;{ let IfTSL = MOD_M_wyvern_DOT_IfTSL;;{ let runtime = MOD_M_wyvern_DOT_runtime;;{ let __temp_390 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_390.prototype.apply= function(cond,tt,ff) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return MOD_M_wyvern_DOT_IfTSL.doif(cond,tt,ff);}
let __temp_391 = new __temp_390();let ifelseARG = __temp_391;;{ let __temp_392 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_392.prototype.apply= function(cond,blocks) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return MOD_M_wyvern_DOT_IfTSL.doifblk(cond,blocks);}
let __temp_393 = new __temp_392();let _if = __temp_393;;let __temp_394 = function() {
let unitSelf = this; };let __temp_395 = new __temp_394();__temp_395; let __temp_403 = function() {
let dontcare = this; };__temp_403.prototype.apply= function(javascript) { let dontcare = this;let __temp_396;{;{ let stdio = FFI_stdio;;{ let __temp_397 = function() {
let var_26 = this; var_26._t_Printable = function() {};};let __temp_398 = new __temp_397();let var_26 = __temp_398;;{ let __temp_399 = function() {
let var_27 = this; };__temp_399.prototype.print= function(text) { let var_27 = this;return stdio.print(text);}

__temp_399.prototype.printInt= function(n) { let var_27 = this;return stdio.printInt(n);}

__temp_399.prototype.printBoolean= function(b) { let var_27 = this;return stdio.printBoolean(b);}

__temp_399.prototype.printFloat= function(f) { let var_27 = this;return stdio.printFloat(f);}

__temp_399.prototype.println= function() { let var_27 = this;return stdio.println();}

__temp_399.prototype.flush= function() { let var_27 = this;return stdio.flush();}
let __temp_400 = new __temp_399();let var_27 = __temp_400;; let __temp_401 = function() {
let var_28 = this; var_28._t_Printable = var_26._t_Printable};__temp_401.prototype.print= function(text) { let var_28 = this;return var_27.print(text);}

__temp_401.prototype.printInt= function(n) { let var_28 = this;return var_27.printInt(n);}

__temp_401.prototype.printBoolean= function(b) { let var_28 = this;return var_27.printBoolean(b);}

__temp_401.prototype.printFloat= function(f) { let var_28 = this;return var_27.printFloat(f);}

__temp_401.prototype.println= function() { let var_28 = this;return var_27.println();}

__temp_401.prototype.flush= function() { let var_28 = this;return var_27.flush();}
let __temp_402 = new __temp_401();__temp_396 = __temp_402;}}}}return __temp_396;}
let __temp_404 = new __temp_403();__temp_385 = __temp_404;}}}}}}let MOD_M_stdout = __temp_385;let __temp_405;{;{ let __temp_408 = function() {
let _this = this; _this._t_Int = _this._t_Int
_this._t_Float = _this._t_Float
_this._t_Unit = function() {};
_this._t_String = _this._t_String
_this._t_Character = _this._t_Character
_this._t_Dyn = function() {};
_this._t_Nothing = function() {};
_this._t_Platform = function() {};
_this._t_Java = function() { let _t_Java = function() {};
_t_Java.prototype = new _this._t_Platform(); return _t_Java;}()
_this._t_Python = function() { let _t_Python = function() {};
_t_Python.prototype = new _this._t_Platform(); return _t_Python;}()
_this._t_JavaScript = function() { let _t_JavaScript = function() {};
_t_JavaScript.prototype = new _this._t_Platform(); return _t_JavaScript;}()
let __temp_406 = function() {
let unitSelf = this; };let __temp_407 = new __temp_406();_this.unit = __temp_407;};let __temp_409 = new __temp_408();let system = __temp_409;;{ let IfTSL = MOD_M_wyvern_DOT_IfTSL;;{ let runtime = MOD_M_wyvern_DOT_runtime;;{ let __temp_410 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_410.prototype.apply= function(cond,tt,ff) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return MOD_M_wyvern_DOT_IfTSL.doif(cond,tt,ff);}
let __temp_411 = new __temp_410();let ifelseARG = __temp_411;;{ let __temp_412 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_412.prototype.apply= function(cond,blocks) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return MOD_M_wyvern_DOT_IfTSL.doifblk(cond,blocks);}
let __temp_413 = new __temp_412();let _if = __temp_413;;let __temp_414 = function() {
let unitSelf = this; };let __temp_415 = new __temp_414();__temp_415;{ let __temp_416 = function() {
let unused = this; };__temp_416.prototype = new system._t_JavaScript();let __temp_417 = new __temp_416();let stdout = MOD_M_stdout.apply(__temp_417);;stdout.print("Hello, world!\n");{ let __temp_418 = function() {
let _this = this; _this.sVar = "Some mutable text.\n";};__temp_418.prototype._getSVar= function() { let _this = this;return (_this).sVar;}

__temp_418.prototype._setSVar= function(x) { let _this = this;return (_this).sVar = x;}
let __temp_419 = new __temp_418();let _tempSVar = __temp_419;;{ let __temp_420 = function() {
let var_29 = this; };__temp_420.prototype._getSVar= function() { let var_29 = this;return (_tempSVar).sVar;}

__temp_420.prototype._setSVar= function(x) { let var_29 = this;return (_tempSVar).sVar = x;}
let __temp_421 = new __temp_420();let var_29 = __temp_421;;{ let sVal = "Some immutable text.\n";;{ let __temp_425 = function() {
let var_30 = this; };__temp_425.prototype.factorial= function(n) { let var_30 = this;let __temp_424;if ((n) > (0)) {let __temp_422;{; __temp_422 = (var_30.factorial((n) - (1))) * (n);}__temp_424 = __temp_422; } else { let __temp_423;{; __temp_423 = 1;}__temp_424 = __temp_423; }return __temp_424;}
let __temp_426 = new __temp_425();let var_30 = __temp_426;;stdout.print("Factorial of 5 is ");stdout.printInt(var_30.factorial(5)); __temp_405 = stdout.println();}}}}}}}}}}}let MOD_M_toplevel = __temp_405;})();