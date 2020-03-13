(function(){"use strict";Number.prototype._PLUS_ = function(x) { return this + x; };Number.prototype._HYPHEN_ = function(x) { return this - x; };Number.prototype._TIMES_ = function(x) { return this * x; };Number.prototype._DIVIDE_ = function(x) { return this / x; };Number.prototype._MOD_ = function(x) { return this % x; };Number.prototype._LESSTHAN_ = function(x) { return this < x; };Number.prototype._GREATERTHAN_ = function(x) { return this > x; };Number.prototype._EQUAL__EQUAL_ = function(x) { return this == x; };Number.prototype.negate = function() { return -this; };Number.prototype.floor = function() { return Math.floor(this); };Boolean.prototype.ifTrue = function(t, f) { return this ? t.apply() : f.apply(); };Boolean.prototype._AMPERSAND__AMPERSAND_ = function(x) { return this && x; };Boolean.prototype._VERTICALBAR__VERTICALBAR_ = function(x) { return this || x; };Boolean.prototype._NOT_ = function() { return !this; };String.prototype._LESSTHAN_ = function(x) { return this < x; };String.prototype._GREATERTHAN_ = function(x) { return this > x; };String.prototype._EQUAL__EQUAL_ = function(x) { return this == x; };String.prototype._PLUS_ = function(x) { return this + x; };String.prototype._length = function(x) { return this.length; };String.prototype.equals = function(x) { return this === x; };const FFI_runtime = require(process.env.WYVERN_HOME + "/native/javascript/stdlib/support/runtime");
const FFI_stdio = require(process.env.WYVERN_HOME + "/native/javascript/stdlib/support/stdio");
const FFI_js = require(process.env.WYVERN_HOME + "/native/javascript/stdlib/js");
const FFI_lexing = require(process.env.WYVERN_HOME + "/native/javascript/stdlib/support/lexing");
const FFI_parsing = require(process.env.WYVERN_HOME + "/native/javascript/stdlib/support/parsing");
let __temp_2 = function() {
let _this = this; _this._t_Rational = _this._t_Rational
_this._t_Int = _this._t_Int
_this._t_Float = _this._t_Float
_this._t_Unit = function() {};
_this._t_String = _this._t_String
_this._t_Character = _this._t_Character
_this._t_Dyn = function() {};
_this._t_Any = function() {};
_this._t_Nothing = function() {};
_this._t_Platform = function() {};
_this._t_Java = function() { let _t_Java = function() {};
_t_Java.prototype = new _this._t_Platform(); return _t_Java;}()
_this._t_Python = function() { let _t_Python = function() {};
_t_Python.prototype = new _this._t_Platform(); return _t_Python;}()
_this._t_JavaScript = function() { let _t_JavaScript = function() {};
_t_JavaScript.prototype = new _this._t_Platform(); return _t_JavaScript;}()
let __temp_0 = function() {
let unitSelf = this; };let __temp_1 = new __temp_0();_this.unit = __temp_1;};let __temp_3 = new __temp_2();let MOD_M_system = __temp_3;let __temp_4;{;{ let system = MOD_M_system;;{ let runtime = FFI_runtime;;{ let __temp_11 = function() {
let var_2 = this; };__temp_11.prototype.assertion= function(description,expression) { let var_2 = this;let __temp_7 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_7.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;let __temp_5 = function() {
let unitSelf = this; };let __temp_6 = new __temp_5();return __temp_6;}
let __temp_8 = new __temp_7();let __temp_9 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_9.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return runtime.fail(description);}
let __temp_10 = new __temp_9();return (expression?__temp_8.apply():__temp_10.apply());}

__temp_11.prototype.fail= function(description) { let var_2 = this;return runtime.fail(description);}
let __temp_12 = new __temp_11();let var_2 = __temp_12;; let __temp_13 = function() {
let var_3 = this; };__temp_13.prototype.assertion= function(description,expression) { let var_3 = this;return var_2.assertion(description,expression);}

__temp_13.prototype.fail= function(description) { let var_3 = this;return var_2.fail(description);}
let __temp_14 = new __temp_13();__temp_4 = __temp_14;}}}}let MOD_M_wyvern_DOT_runtime = __temp_4;let __temp_15;{;{ let system = MOD_M_system;;{ let runtime = MOD_M_wyvern_DOT_runtime;;{ let __temp_26 = function() {
let var_4 = this; var_4._t_Option = function() {};
var_4._t_Some = function() { let _t_Some = function() {};
_t_Some.prototype = new var_4._t_Option(); return _t_Some;}()
var_4._t_None = function() { let _t_None = function() {};
_t_None.prototype = new var_4._t_Option(); return _t_None;}()};__temp_26.prototype.Some= function(__generic__T,x) { let var_4 = this;let __temp_18 = function() {
let _this = this; _this._t_T = __generic__T._t_T
_this.content = x;
_this.value = x;
_this.isDefined = true;};__temp_18.prototype = new var_4._t_Some();__temp_18.prototype.map= function(__generic__U,f) { let _this = this;let __temp_16 = function() {
let dontcare = this; dontcare._t_T = __generic__U._t_U};let __temp_17 = new __temp_16();return var_4.Some(__temp_17,f.apply(x));}

__temp_18.prototype.flatMap= function(__generic__U,f) { let _this = this;return f.apply(x);}

__temp_18.prototype.getOrElse= function(defaultValue) { let _this = this;return x;}

__temp_18.prototype.get= function() { let _this = this;return x;}

__temp_18.prototype.orElse= function(x) { let _this = this;return _this;}
let __temp_19 = new __temp_18();return __temp_19;}

__temp_26.prototype.None= function(__generic__T) { let var_4 = this;let __temp_24 = function() {
let _this = this; _this._t_T = __generic__T._t_T
_this.isDefined = false;};__temp_24.prototype = new var_4._t_None();__temp_24.prototype.map= function(__generic__U,f) { let _this = this;let __temp_20 = function() {
let dontcare = this; dontcare._t_T = __generic__U._t_U};let __temp_21 = new __temp_20();return var_4.None(__temp_21);}

__temp_24.prototype.flatMap= function(__generic__U,f) { let _this = this;let __temp_22 = function() {
let dontcare = this; dontcare._t_T = __generic__U._t_U};let __temp_23 = new __temp_22();return var_4.None(__temp_23);}

__temp_24.prototype.getOrElse= function(defaultValue) { let _this = this;return defaultValue.apply();}

__temp_24.prototype.get= function() { let _this = this;return MOD_M_wyvern_DOT_runtime.fail("called get() on an Option that was None");}

__temp_24.prototype.orElse= function(x) { let _this = this;return x.apply();}
let __temp_25 = new __temp_24();return __temp_25;}
let __temp_27 = new __temp_26();let var_4 = __temp_27;; let __temp_28 = function() {
let var_5 = this; var_5._t_Option = var_4._t_Option
var_5._t_Some = var_4._t_Some
var_5._t_None = var_4._t_None};__temp_28.prototype.Some= function(__generic__T,x) { let var_5 = this;return var_4.Some(__generic__T,x);}

__temp_28.prototype.None= function(__generic__T) { let var_5 = this;return var_4.None(__generic__T);}
let __temp_29 = new __temp_28();__temp_15 = __temp_29;}}}}let MOD_M_wyvern_DOT_option = __temp_15;let __temp_30;{;{ let system = MOD_M_system;;{ let option = MOD_M_wyvern_DOT_option;;{ let __temp_31 = function() {
let var_6 = this; var_6._t_Option = MOD_M_wyvern_DOT_option._t_Option};let __temp_32 = new __temp_31();let var_6 = __temp_32;;{ let __temp_164 = function() {
let var_7 = this; var_7._t_List = function() {};};__temp_164.prototype.make= function(__generic__E) { let var_7 = this;let __temp_33;{;{ let __temp_73 = function() {
let var_8 = this; var_8._t_Cell = function() {};};__temp_73.prototype.makeCell= function(e,n) { let var_8 = this;let __temp_69 = function() {
let self = this; self.element = e;
self.next = n;};__temp_69.prototype._getElement= function() { let self = this;return (self).element;}

__temp_69.prototype._setElement= function(x) { let self = this;return (self).element = x;}

__temp_69.prototype._getNext= function() { let self = this;return (self).next;}

__temp_69.prototype._setNext= function(x) { let self = this;return (self).next = x;}

__temp_69.prototype.find= function(pred) { let self = this;let __temp_36 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_36.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;let __temp_34 = function() {
let dontcare = this; dontcare._t_T = __generic__E._t_E};let __temp_35 = new __temp_34();return MOD_M_wyvern_DOT_option.Some(__temp_35,(self).element);}
let __temp_37 = new __temp_36();let __temp_42 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_42.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;let __temp_38 = function() {
let dontcare = this; dontcare._t_U = __generic__E._t_E};let __temp_39 = new __temp_38();let __temp_40 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_40.prototype.apply= function(c) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return c.find(pred);}
let __temp_41 = new __temp_40();return (self).next.flatMap(__temp_39,__temp_41);}
let __temp_43 = new __temp_42();return (pred.apply((self).element)?__temp_37.apply():__temp_43.apply());}

__temp_69.prototype.get= function(n) { let self = this;let __temp_46 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_46.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;let __temp_44 = function() {
let dontcare = this; dontcare._t_T = __generic__E._t_E};let __temp_45 = new __temp_44();return MOD_M_wyvern_DOT_option.Some(__temp_45,(self).element);}
let __temp_47 = new __temp_46();let __temp_52 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_52.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;let __temp_48 = function() {
let dontcare = this; dontcare._t_U = __generic__E._t_E};let __temp_49 = new __temp_48();let __temp_50 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_50.prototype.apply= function(c) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return c.get((n) - (1));}
let __temp_51 = new __temp_50();return (self).next.flatMap(__temp_49,__temp_51);}
let __temp_53 = new __temp_52();return ((n) == (0)?__temp_47.apply():__temp_53.apply());}

__temp_69.prototype.getCell= function(n) { let self = this;let __temp_56 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_56.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;let __temp_54 = function() {
let dontcare = this; dontcare._t_T = var_8._t_Cell};let __temp_55 = new __temp_54();return MOD_M_wyvern_DOT_option.Some(__temp_55,self);}
let __temp_57 = new __temp_56();let __temp_62 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_62.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;let __temp_58 = function() {
let dontcare = this; dontcare._t_U = var_8._t_Cell};let __temp_59 = new __temp_58();let __temp_60 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_60.prototype.apply= function(c) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return c.getCell((n) - (1));}
let __temp_61 = new __temp_60();return (self).next.flatMap(__temp_59,__temp_61);}
let __temp_63 = new __temp_62();return ((n) == (0)?__temp_57.apply():__temp_63.apply());}

__temp_69.prototype.do= function(f) { let self = this;let __temp_64;{;f.apply((self).element); let __temp_65 = function() {
let dontcare = this; dontcare._t_U = function() {};};let __temp_66 = new __temp_65();let __temp_67 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_67.prototype.apply= function(c) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return c.do(f);}
let __temp_68 = new __temp_67();__temp_64 = (self).next.map(__temp_66,__temp_68);}return __temp_64;}
let __temp_70 = new __temp_69();return __temp_70;}

__temp_73.prototype.makeOneCell= function(e) { let var_8 = this;let __temp_71 = function() {
let dontcare = this; dontcare._t_T = var_8._t_Cell};let __temp_72 = new __temp_71();return var_8.makeCell(e,MOD_M_wyvern_DOT_option.None(__temp_72));}
let __temp_74 = new __temp_73();let var_8 = __temp_74;; let __temp_160 = function() {
let self = this; self._t_E = __generic__E._t_E
let __temp_75 = function() {
let dontcare = this; dontcare._t_T = var_8._t_Cell};let __temp_76 = new __temp_75();self.first = MOD_M_wyvern_DOT_option.None(__temp_76);
let __temp_77 = function() {
let dontcare = this; dontcare._t_T = var_8._t_Cell};let __temp_78 = new __temp_77();self.last = MOD_M_wyvern_DOT_option.None(__temp_78);
self.size = 0;};__temp_160.prototype._getFirst= function() { let self = this;return (self).first;}

__temp_160.prototype._setFirst= function(x) { let self = this;return (self).first = x;}

__temp_160.prototype._getLast= function() { let self = this;return (self).last;}

__temp_160.prototype._setLast= function(x) { let self = this;return (self).last = x;}

__temp_160.prototype._getSize= function() { let self = this;return (self).size;}

__temp_160.prototype._setSize= function(x) { let self = this;return (self).size = x;}

__temp_160.prototype.append= function(e) { let self = this;let __temp_79;{;{ let __temp_92 = function() {
let var_9 = this; };__temp_92.prototype.thenCase= function() { let var_9 = this;let __temp_80;{;{ let cell = var_8.makeOneCell(e);;let __temp_81 = function() {
let dontcare = this; dontcare._t_U = function() {};};let __temp_82 = new __temp_81();let __temp_85 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_85.prototype.apply= function(c) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;let __temp_83 = function() {
let dontcare = this; dontcare._t_T = var_8._t_Cell};let __temp_84 = new __temp_83();return (c).next = MOD_M_wyvern_DOT_option.Some(__temp_84,cell);}
let __temp_86 = new __temp_85();(self).last.map(__temp_82,__temp_86); let __temp_87 = function() {
let dontcare = this; dontcare._t_T = var_8._t_Cell};let __temp_88 = new __temp_87();__temp_80 = (self).last = MOD_M_wyvern_DOT_option.Some(__temp_88,cell);}}return __temp_80;}

__temp_92.prototype.elseCase= function() { let var_9 = this;let __temp_89;{;let __temp_90 = function() {
let dontcare = this; dontcare._t_T = var_8._t_Cell};let __temp_91 = new __temp_90();(self).first = MOD_M_wyvern_DOT_option.Some(__temp_91,var_8.makeOneCell(e)); __temp_89 = (self).last = (self).first;}return __temp_89;}
let __temp_93 = new __temp_92();let var_9 = __temp_93;;let __temp_94 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_94.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return var_9.thenCase();}
let __temp_95 = new __temp_94();let __temp_96 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_96.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return var_9.elseCase();}
let __temp_97 = new __temp_96();(((self).first).isDefined?__temp_95.apply():__temp_97.apply()); __temp_79 = (self).size = ((self).size) + (1);}}return __temp_79;}

__temp_160.prototype.appendAll= function(other) { let self = this;let __temp_98 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_98.prototype.apply= function(e) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return self.append(e);}
let __temp_99 = new __temp_98();return other.do(__temp_99);}

__temp_160.prototype.find= function(pred) { let self = this;let __temp_100 = function() {
let dontcare = this; dontcare._t_U = __generic__E._t_E};let __temp_101 = new __temp_100();let __temp_102 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_102.prototype.apply= function(c) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return c.find(pred);}
let __temp_103 = new __temp_102();return (self).first.flatMap(__temp_101,__temp_103);}

__temp_160.prototype._length= function() { let self = this;return (self).size;}

__temp_160.prototype.get= function(n) { let self = this;let __temp_104 = function() {
let dontcare = this; dontcare._t_U = __generic__E._t_E};let __temp_105 = new __temp_104();let __temp_106 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_106.prototype.apply= function(c) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return c.get(n);}
let __temp_107 = new __temp_106();return (self).first.flatMap(__temp_105,__temp_107);}

__temp_160.prototype.remove= function(n) { let self = this;let __temp_108;{;{ let __temp_145 = function() {
let var_10 = this; };__temp_145.prototype.definitelyRemove= function() { let var_10 = this;let __temp_109;{;{ let __temp_112 = function() {
let _this = this; let __temp_110 = function() {
let dontcare = this; dontcare._t_T = var_8._t_Cell};let __temp_111 = new __temp_110();_this.lastCell = MOD_M_wyvern_DOT_option.None(__temp_111);};__temp_112.prototype._getLastCell= function() { let _this = this;return (_this).lastCell;}

__temp_112.prototype._setLastCell= function(x) { let _this = this;return (_this).lastCell = x;}
let __temp_113 = new __temp_112();let _tempLastCell = __temp_113;;{ let __temp_114 = function() {
let var_13 = this; };__temp_114.prototype._getLastCell= function() { let var_13 = this;return (_tempLastCell).lastCell;}

__temp_114.prototype._setLastCell= function(x) { let var_13 = this;return (_tempLastCell).lastCell = x;}
let __temp_115 = new __temp_114();let var_13 = __temp_115;;{ let __temp_129 = function() {
let var_14 = this; };__temp_129.prototype.removeInMiddle= function() { let var_14 = this;let __temp_116;{;{ let __temp_117 = function() {
let dontcare = this; dontcare._t_U = var_8._t_Cell};let __temp_118 = new __temp_117();let __temp_119 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_119.prototype.apply= function(c) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return c.getCell((n) - (1));}
let __temp_120 = new __temp_119();let cellBefore = (self).first.flatMap(__temp_118,__temp_120);;let __temp_121 = function() {
let dontcare = this; dontcare._t_U = function() {};};let __temp_122 = new __temp_121();let __temp_127 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_127.prototype.apply= function(c) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;let __temp_123 = function() {
let dontcare = this; dontcare._t_U = var_8._t_Cell};let __temp_124 = new __temp_123();let __temp_125 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_125.prototype.apply= function(c2) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return (c2).next;}
let __temp_126 = new __temp_125();return (c).next = (c).next.flatMap(__temp_124,__temp_126);}
let __temp_128 = new __temp_127();cellBefore.map(__temp_122,__temp_128); __temp_116 = var_13._setLastCell(cellBefore);}}return __temp_116;}
let __temp_130 = new __temp_129();let var_14 = __temp_130;;let __temp_135 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_135.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;let __temp_131 = function() {
let dontcare = this; dontcare._t_U = var_8._t_Cell};let __temp_132 = new __temp_131();let __temp_133 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_133.prototype.apply= function(c) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return (c).next;}
let __temp_134 = new __temp_133();return (self).first = (self).first.flatMap(__temp_132,__temp_134);}
let __temp_136 = new __temp_135();let __temp_137 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_137.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return var_14.removeInMiddle();}
let __temp_138 = new __temp_137();((n) == (0)?__temp_136.apply():__temp_138.apply());let __temp_139 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_139.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return (self).last = var_13._getLastCell();}
let __temp_140 = new __temp_139();let __temp_143 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_143.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;let __temp_141 = function() {
let unitSelf = this; };let __temp_142 = new __temp_141();return __temp_142;}
let __temp_144 = new __temp_143();((n) == (((self).size) - (1))?__temp_140.apply():__temp_144.apply());(self).size = ((self).size) - (1); __temp_109 = true;}}}}return __temp_109;}
let __temp_146 = new __temp_145();let var_10 = __temp_146;; let __temp_147 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_147.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return false;}
let __temp_148 = new __temp_147();let __temp_149 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_149.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return var_10.definitelyRemove();}
let __temp_150 = new __temp_149();__temp_108 = (((n) > (((self).size) - (1))) || ((n) < (0))?__temp_148.apply():__temp_150.apply());}}return __temp_108;}

__temp_160.prototype.do= function(f) { let self = this;let __temp_151 = function() {
let dontcare = this; dontcare._t_U = function() {};};let __temp_152 = new __temp_151();let __temp_153 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_153.prototype.apply= function(c) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return c.do(f);}
let __temp_154 = new __temp_153();return (self).first.map(__temp_152,__temp_154);}

__temp_160.prototype.map= function(__generic__F,f) { let self = this;let __temp_155;{;{ let __temp_156 = function() {
let dontcare = this; dontcare._t_E = __generic__F._t_F};let __temp_157 = new __temp_156();let newList = var_7.make(__temp_157);;let __temp_158 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_158.prototype.apply= function(e) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return newList.append(f.apply(e));}
let __temp_159 = new __temp_158();self.do(__temp_159); __temp_155 = newList;}}return __temp_155;}
let __temp_161 = new __temp_160();__temp_33 = __temp_161;}}return __temp_33;}

__temp_164.prototype.makeD= function() { let var_7 = this;let __temp_162 = function() {
let dontcare = this; dontcare._t_E = function() {};};let __temp_163 = new __temp_162();return var_7.make(__temp_163);}
let __temp_165 = new __temp_164();let var_7 = __temp_165;; let __temp_166 = function() {
let var_15 = this; var_15._t_Option = var_6._t_Option
var_15._t_List = var_7._t_List};__temp_166.prototype.make= function(__generic__E) { let var_15 = this;return var_7.make(__generic__E);}

__temp_166.prototype.makeD= function() { let var_15 = this;return var_7.makeD();}
let __temp_167 = new __temp_166();__temp_30 = __temp_167;}}}}}let MOD_M_wyvern_DOT_internal_DOT_list = __temp_30;let __temp_168;{;{ let system = MOD_M_system;;{ let option = MOD_M_wyvern_DOT_option;;{ let __temp_169 = function() {
let unitSelf = this; };let __temp_170 = new __temp_169();let utils = __temp_170;;{ let __temp_175 = function() {
let var_20 = this; var_20._t_Regex = function() {};
var_20._t_Match = function() {};};__temp_175.prototype.makeMatch= function(m,a) { let var_20 = this;let __temp_171 = function() {
let _this = this; };__temp_171.prototype.matched= function() { let _this = this;return m;}

__temp_171.prototype.after= function() { let _this = this;return a;}
let __temp_172 = new __temp_171();return __temp_172;}

__temp_175.prototype.apply= function(regex) { let var_20 = this;let __temp_173 = function() {
let _this = this; };__temp_173.prototype.findPrefixOf= function(source) { let _this = this;return utils.findPrefixOf(regex,source);}

__temp_173.prototype.findPrefixMatchOf= function(source) { let _this = this;return utils.findPrefixMatchOf(regex,source);}
let __temp_174 = new __temp_173();return __temp_174;}
let __temp_176 = new __temp_175();let var_20 = __temp_176;; let __temp_177 = function() {
let var_21 = this; var_21._t_Regex = var_20._t_Regex
var_21._t_Match = var_20._t_Match};__temp_177.prototype.makeMatch= function(m,a) { let var_21 = this;return var_20.makeMatch(m,a);}

__temp_177.prototype.apply= function(regex) { let var_21 = this;return var_20.apply(regex);}
let __temp_178 = new __temp_177();__temp_168 = __temp_178;}}}}}let MOD_M_wyvern_DOT_util_DOT_matching_DOT_regexInternal = __temp_168;let __temp_179;{;{ let system = MOD_M_system;;{ let list = MOD_M_wyvern_DOT_internal_DOT_list;;{ let __temp_180 = function() {
let unitSelf = this; };let __temp_181 = new __temp_180();let utils = __temp_181;;{ let __temp_252 = function() {
let var_16 = this; var_16._t_AST = function() {};
var_16._t_Decl = function() {};
var_16._t_Type = function() {};
var_16._t_DeclType = function() {};
var_16._t_VarBinding = function() {};
var_16._t_Case = function() {};
var_16._t_FormalArg = function() {};};__temp_252.prototype.varBinding= function(varName,varType,toReplace) { let var_16 = this;let __temp_182 = function() {
let _this = this; _this.binding = utils.varBinding(varName,varType,toReplace);};let __temp_183 = new __temp_182();return __temp_183;}

__temp_252.prototype.makeCase= function(varName,pattern,body) { let var_16 = this;let __temp_184 = function() {
let _this = this; _this.caseValue = utils.makeCase(varName,pattern,body);};let __temp_185 = new __temp_184();return __temp_185;}

__temp_252.prototype.formalArg= function(name,argType) { let var_16 = this;let __temp_186 = function() {
let _this = this; _this.formalArg = utils.formalArg(name,argType);};let __temp_187 = new __temp_186();return __temp_187;}

__temp_252.prototype.bind= function(bindings,inExpr) { let var_16 = this;let __temp_188 = function() {
let _this = this; _this.ast = utils.bind(bindings,inExpr);};let __temp_189 = new __temp_188();return __temp_189;}

__temp_252.prototype.object= function(decls) { let var_16 = this;let __temp_190 = function() {
let _this = this; _this.ast = utils.object(decls);};let __temp_191 = new __temp_190();return __temp_191;}

__temp_252.prototype.defDeclaration= function(name,formalArgs,returnType,body) { let var_16 = this;let __temp_192 = function() {
let _this = this; _this.decl = utils.defDeclaration(name,formalArgs,returnType,body);};let __temp_193 = new __temp_192();return __temp_193;}

__temp_252.prototype.forwardDeclaration= function(forwardType,fieldName) { let var_16 = this;let __temp_194 = function() {
let _this = this; _this.decl = utils.forwardDeclaration(forwardType,fieldName);};let __temp_195 = new __temp_194();return __temp_195;}

__temp_252.prototype.moduleDeclaration= function(name,formalArgs,moduleType,body,dependencies) { let var_16 = this;let __temp_196 = function() {
let _this = this; _this.decl = utils.moduleDeclaration(name,formalArgs,moduleType,body,dependencies);};let __temp_197 = new __temp_196();return __temp_197;}

__temp_252.prototype.typeDeclaration= function(typeName,sourceType) { let var_16 = this;let __temp_198 = function() {
let _this = this; _this.decl = utils.typeDeclaration(typeName,sourceType);};let __temp_199 = new __temp_198();return __temp_199;}

__temp_252.prototype.valDeclaration= function(fieldName,fieldType,value) { let var_16 = this;let __temp_200 = function() {
let _this = this; _this.decl = utils.valDeclaration(fieldName,fieldType,value);};let __temp_201 = new __temp_200();return __temp_201;}

__temp_252.prototype.varDeclaration= function(fieldName,fieldType,value) { let var_16 = this;let __temp_202 = function() {
let _this = this; _this.decl = utils.varDeclaration(fieldName,fieldType,value);};let __temp_203 = new __temp_202();return __temp_203;}

__temp_252.prototype.int= function(i) { let var_16 = this;let __temp_204 = function() {
let _this = this; _this.ast = utils.intLiteral(i);};let __temp_205 = new __temp_204();return __temp_205;}

__temp_252.prototype.boolean= function(b) { let var_16 = this;let __temp_206 = function() {
let _this = this; _this.ast = utils.booleanLiteral(b);};let __temp_207 = new __temp_206();return __temp_207;}

__temp_252.prototype.string= function(s) { let var_16 = this;let __temp_208 = function() {
let _this = this; _this.ast = utils.stringLiteral(s);};let __temp_209 = new __temp_208();return __temp_209;}

__temp_252.prototype.variable= function(s) { let var_16 = this;let __temp_210 = function() {
let _this = this; _this.ast = utils.variable(s);};let __temp_211 = new __temp_210();return __temp_211;}

__temp_252.prototype.call= function(receiver,methodName,_arguments) { let var_16 = this;let __temp_212 = function() {
let _this = this; _this.ast = utils.methodCall(receiver,methodName,_arguments);};let __temp_213 = new __temp_212();return __temp_213;}

__temp_252.prototype.cast= function(toCastExpr,exprType) { let var_16 = this;let __temp_214 = function() {
let _this = this; _this.ast = utils.cast(toCastExpr,exprType);};let __temp_215 = new __temp_214();return __temp_215;}

__temp_252.prototype.ffi= function(importName,importType) { let var_16 = this;let __temp_216 = function() {
let _this = this; _this.ast = utils.ffi(importName,importType);};let __temp_217 = new __temp_216();return __temp_217;}

__temp_252.prototype.ffiImport= function(ffiType,path,importType) { let var_16 = this;let __temp_218 = function() {
let _this = this; _this.ast = utils.ffiImport(ffiType,path,importType);};let __temp_219 = new __temp_218();return __temp_219;}

__temp_252.prototype.fieldGet= function(objectExpr,fieldName) { let var_16 = this;let __temp_220 = function() {
let _this = this; _this.ast = utils.fieldGet(objectExpr,fieldName);};let __temp_221 = new __temp_220();return __temp_221;}

__temp_252.prototype.fieldSet= function(exprType,object,fieldName,exprToAssign) { let var_16 = this;let __temp_222 = function() {
let _this = this; _this.ast = utils.fieldSet(exprType,object,fieldName,exprToAssign);};let __temp_223 = new __temp_222();return __temp_223;}

__temp_252.prototype.matchExpr= function(matchExpr,elseExpr,cases) { let var_16 = this;let __temp_224 = function() {
let _this = this; _this.ast = utils.matchExpr(matchExpr,elseExpr,cases);};let __temp_225 = new __temp_224();return __temp_225;}

__temp_252.prototype.abstractTypeMember= function(name,isResource) { let var_16 = this;let __temp_226 = function() {
let _this = this; _this.declType = utils.abstractTypeMember(name,isResource);};let __temp_227 = new __temp_226();return __temp_227;}

__temp_252.prototype.concreteTypeMember= function(name,sourceType) { let var_16 = this;let __temp_228 = function() {
let _this = this; _this.declType = utils.concreteTypeMember(name,sourceType);};let __temp_229 = new __temp_228();return __temp_229;}

__temp_252.prototype.defDeclType= function(methodName,returnType,formalArgs) { let var_16 = this;let __temp_230 = function() {
let _this = this; _this.declType = utils.defDeclType(methodName,returnType,formalArgs);};let __temp_231 = new __temp_230();return __temp_231;}

__temp_252.prototype.valDeclType= function(field,valType) { let var_16 = this;let __temp_232 = function() {
let _this = this; _this.declType = utils.valDeclType(field,valType);};let __temp_233 = new __temp_232();return __temp_233;}

__temp_252.prototype.varDeclType= function(field,varType) { let var_16 = this;let __temp_234 = function() {
let _this = this; _this.declType = utils.varDeclType(field,varType);};let __temp_235 = new __temp_234();return __temp_235;}

__temp_252.prototype.parseExpression= function(input,ctx) { let var_16 = this;let __temp_236;{;{ let ctxDyn = ctx;; let __temp_237 = function() {
let _this = this; _this.ast = utils.parseExpression(input,ctxDyn);};let __temp_238 = new __temp_237();__temp_236 = __temp_238;}}return __temp_236;}

__temp_252.prototype.parseExpressionNoContext= function(input) { let var_16 = this;let __temp_239 = function() {
let _this = this; _this.ast = utils.parseExpressionNoContext(input);};let __temp_240 = new __temp_239();return __temp_240;}

__temp_252.prototype.parseGeneratedModule= function(input) { let var_16 = this;let __temp_241 = function() {
let _this = this; _this.ast = utils.parseGeneratedModule(input);};let __temp_242 = new __temp_241();return __temp_242;}

__temp_252.prototype.parseExpressionList= function(input,ctx) { let var_16 = this;let __temp_243;{;{ let ctxDyn = ctx;;{ let __temp_246 = function() {
let var_17 = this; };__temp_246.prototype.javaASTToWyvAST= function(jAST) { let var_17 = this;let __temp_244 = function() {
let _this = this; _this.ast = jAST;};let __temp_245 = new __temp_244();return __temp_245;}
let __temp_247 = new __temp_246();let var_17 = __temp_247;;{ let l = utils.parseExpressionList(input,ctxDyn);; let __temp_248 = function() {
let dontcare = this; dontcare._t_F = var_16._t_AST};let __temp_249 = new __temp_248();let __temp_250 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_250.prototype.apply= function(ast) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return var_17.javaASTToWyvAST(ast);}
let __temp_251 = new __temp_250();__temp_243 = l.map(__temp_249,__temp_251);}}}}return __temp_243;}
let __temp_253 = new __temp_252();let var_16 = __temp_253;;{ let __temp_273 = function() {
let _this = this; };__temp_273.prototype.int= function() { let _this = this;let __temp_254 = function() {
let _this = this; _this.typ = utils.intType();};let __temp_255 = new __temp_254();return __temp_255;}

__temp_273.prototype.dyn= function() { let _this = this;let __temp_256 = function() {
let _this = this; _this.typ = utils.dynType();};let __temp_257 = new __temp_256();return __temp_257;}

__temp_273.prototype.unit= function() { let _this = this;let __temp_258 = function() {
let _this = this; _this.typ = utils.unitType();};let __temp_259 = new __temp_258();return __temp_259;}

__temp_273.prototype.boolean= function() { let _this = this;let __temp_260 = function() {
let _this = this; _this.typ = utils.booleanType();};let __temp_261 = new __temp_260();return __temp_261;}

__temp_273.prototype.string= function() { let _this = this;let __temp_262 = function() {
let _this = this; _this.typ = utils.stringType();};let __temp_263 = new __temp_262();return __temp_263;}

__temp_273.prototype.float= function() { let _this = this;let __temp_264 = function() {
let _this = this; _this.typ = utils.floatType();};let __temp_265 = new __temp_264();return __temp_265;}

__temp_273.prototype.nominal= function(pathVariable,typeMember) { let _this = this;let __temp_266 = function() {
let _this = this; _this.typ = utils.nominalType(pathVariable,typeMember);};let __temp_267 = new __temp_266();return __temp_267;}

__temp_273.prototype.structural= function(selfName,declTypes) { let _this = this;let __temp_268 = function() {
let _this = this; _this.typ = utils.structuralType(selfName,declTypes);};let __temp_269 = new __temp_268();return __temp_269;}

__temp_273.prototype.refinement= function(typeParams,base) { let _this = this;let __temp_270 = function() {
let _this = this; _this.typ = utils.refinementType(typeParams,base);};let __temp_271 = new __temp_270();return __temp_271;}

__temp_273.prototype.equals= function(t1,t2,ctx) { let _this = this;let __temp_272;{;{ let ctxDyn = ctx;; __temp_272 = utils.checkTypeEquality(t1,t2,ctxDyn);}}return __temp_272;}
let __temp_274 = new __temp_273();let types = __temp_274;;{ let __temp_283 = function() {
let var_18 = this; };__temp_283.prototype.stripLeadingWhitespace= function(input,mustStrip) { let var_18 = this;return utils.stripLeadingWhitespace(input,mustStrip);}

__temp_283.prototype.genIdent= function() { let var_18 = this;return utils.genIdent();}

__temp_283.prototype.let= function(ident,bindingType,bindingValue,inExpr) { let var_18 = this;let __temp_275 = function() {
let _this = this; _this.ast = utils.let(ident,bindingType,bindingValue,inExpr);};let __temp_276 = new __temp_275();return __temp_276;}

__temp_283.prototype.getMetadataTypeReceiver= function(ctx) { let var_18 = this;let __temp_277;{;{ let ctxDyn = ctx;; let __temp_278 = function() {
let _this = this; _this.ast = utils.getMetadataTypeReceiver(ctxDyn);};let __temp_279 = new __temp_278();__temp_277 = __temp_279;}}return __temp_277;}

__temp_283.prototype.getType= function(e,ctx) { let var_18 = this;let __temp_280;{;{ let ctxDyn = ctx;; let __temp_281 = function() {
let _this = this; _this.typ = utils.getObjectType(e,ctxDyn);};let __temp_282 = new __temp_281();__temp_280 = __temp_282;}}return __temp_280;}
let __temp_284 = new __temp_283();let var_18 = __temp_284;; let __temp_285 = function() {
let var_19 = this; var_19._t_AST = var_16._t_AST
var_19._t_Decl = var_16._t_Decl
var_19._t_Type = var_16._t_Type
var_19._t_DeclType = var_16._t_DeclType
var_19._t_VarBinding = var_16._t_VarBinding
var_19._t_Case = var_16._t_Case
var_19._t_FormalArg = var_16._t_FormalArg
var_19.types = types;};__temp_285.prototype.varBinding= function(varName,varType,toReplace) { let var_19 = this;return var_16.varBinding(varName,varType,toReplace);}

__temp_285.prototype.makeCase= function(varName,pattern,body) { let var_19 = this;return var_16.makeCase(varName,pattern,body);}

__temp_285.prototype.formalArg= function(name,argType) { let var_19 = this;return var_16.formalArg(name,argType);}

__temp_285.prototype.bind= function(bindings,inExpr) { let var_19 = this;return var_16.bind(bindings,inExpr);}

__temp_285.prototype.object= function(decls) { let var_19 = this;return var_16.object(decls);}

__temp_285.prototype.defDeclaration= function(name,formalArgs,returnType,body) { let var_19 = this;return var_16.defDeclaration(name,formalArgs,returnType,body);}

__temp_285.prototype.forwardDeclaration= function(forwardType,fieldName) { let var_19 = this;return var_16.forwardDeclaration(forwardType,fieldName);}

__temp_285.prototype.moduleDeclaration= function(name,formalArgs,moduleType,body,dependencies) { let var_19 = this;return var_16.moduleDeclaration(name,formalArgs,moduleType,body,dependencies);}

__temp_285.prototype.typeDeclaration= function(typeName,sourceType) { let var_19 = this;return var_16.typeDeclaration(typeName,sourceType);}

__temp_285.prototype.valDeclaration= function(fieldName,fieldType,value) { let var_19 = this;return var_16.valDeclaration(fieldName,fieldType,value);}

__temp_285.prototype.varDeclaration= function(fieldName,fieldType,value) { let var_19 = this;return var_16.varDeclaration(fieldName,fieldType,value);}

__temp_285.prototype.int= function(i) { let var_19 = this;return var_16.int(i);}

__temp_285.prototype.boolean= function(b) { let var_19 = this;return var_16.boolean(b);}

__temp_285.prototype.string= function(s) { let var_19 = this;return var_16.string(s);}

__temp_285.prototype.variable= function(s) { let var_19 = this;return var_16.variable(s);}

__temp_285.prototype.call= function(receiver,methodName,_arguments) { let var_19 = this;return var_16.call(receiver,methodName,_arguments);}

__temp_285.prototype.cast= function(toCastExpr,exprType) { let var_19 = this;return var_16.cast(toCastExpr,exprType);}

__temp_285.prototype.ffi= function(importName,importType) { let var_19 = this;return var_16.ffi(importName,importType);}

__temp_285.prototype.ffiImport= function(ffiType,path,importType) { let var_19 = this;return var_16.ffiImport(ffiType,path,importType);}

__temp_285.prototype.fieldGet= function(objectExpr,fieldName) { let var_19 = this;return var_16.fieldGet(objectExpr,fieldName);}

__temp_285.prototype.fieldSet= function(exprType,object,fieldName,exprToAssign) { let var_19 = this;return var_16.fieldSet(exprType,object,fieldName,exprToAssign);}

__temp_285.prototype.matchExpr= function(matchExpr,elseExpr,cases) { let var_19 = this;return var_16.matchExpr(matchExpr,elseExpr,cases);}

__temp_285.prototype.abstractTypeMember= function(name,isResource) { let var_19 = this;return var_16.abstractTypeMember(name,isResource);}

__temp_285.prototype.concreteTypeMember= function(name,sourceType) { let var_19 = this;return var_16.concreteTypeMember(name,sourceType);}

__temp_285.prototype.defDeclType= function(methodName,returnType,formalArgs) { let var_19 = this;return var_16.defDeclType(methodName,returnType,formalArgs);}

__temp_285.prototype.valDeclType= function(field,valType) { let var_19 = this;return var_16.valDeclType(field,valType);}

__temp_285.prototype.varDeclType= function(field,varType) { let var_19 = this;return var_16.varDeclType(field,varType);}

__temp_285.prototype.parseExpression= function(input,ctx) { let var_19 = this;return var_16.parseExpression(input,ctx);}

__temp_285.prototype.parseExpressionNoContext= function(input) { let var_19 = this;return var_16.parseExpressionNoContext(input);}

__temp_285.prototype.parseGeneratedModule= function(input) { let var_19 = this;return var_16.parseGeneratedModule(input);}

__temp_285.prototype.parseExpressionList= function(input,ctx) { let var_19 = this;return var_16.parseExpressionList(input,ctx);}

__temp_285.prototype.stripLeadingWhitespace= function(input,mustStrip) { let var_19 = this;return var_18.stripLeadingWhitespace(input,mustStrip);}

__temp_285.prototype.genIdent= function() { let var_19 = this;return var_18.genIdent();}

__temp_285.prototype.let= function(ident,bindingType,bindingValue,inExpr) { let var_19 = this;return var_18.let(ident,bindingType,bindingValue,inExpr);}

__temp_285.prototype.getMetadataTypeReceiver= function(ctx) { let var_19 = this;return var_18.getMetadataTypeReceiver(ctx);}

__temp_285.prototype.getType= function(e,ctx) { let var_19 = this;return var_18.getType(e,ctx);}
let __temp_286 = new __temp_285();__temp_179 = __temp_286;}}}}}}}let MOD_M_wyvern_DOT_internal_DOT_ast = __temp_179;let __temp_287;{;{ let system = MOD_M_system;;{ let __temp_288 = function() {
let unitSelf = this; };let __temp_289 = new __temp_288();let debug = __temp_289;;{ let __temp_290 = function() {
let var_0 = this; };__temp_290.prototype.print= function(text) { let var_0 = this;return debug.print(text);}

__temp_290.prototype.printInt= function(n) { let var_0 = this;return debug.printInt(n);}

__temp_290.prototype.println= function() { let var_0 = this;return debug.println();}
let __temp_291 = new __temp_290();let var_0 = __temp_291;; let __temp_292 = function() {
let var_1 = this; };__temp_292.prototype.print= function(text) { let var_1 = this;return var_0.print(text);}

__temp_292.prototype.printInt= function(n) { let var_1 = this;return var_0.printInt(n);}

__temp_292.prototype.println= function() { let var_1 = this;return var_0.println();}
let __temp_293 = new __temp_292();__temp_287 = __temp_293;}}}}let MOD_M_platform_DOT_java_DOT_debug = __temp_287;let __temp_294;{;{ let system = MOD_M_system;;{ let debug = MOD_M_platform_DOT_java_DOT_debug;;{ let ast = MOD_M_wyvern_DOT_internal_DOT_ast;;{ let option = MOD_M_wyvern_DOT_option;;{ let regexInternal = MOD_M_wyvern_DOT_util_DOT_matching_DOT_regexInternal;;{ let list = MOD_M_wyvern_DOT_internal_DOT_list;;{ let regex = MOD_M_wyvern_DOT_util_DOT_matching_DOT_regexInternal;;{ let __temp_295 = function() {
let var_22 = this; var_22._t_AST = MOD_M_wyvern_DOT_internal_DOT_ast._t_AST};let __temp_296 = new __temp_295();let var_22 = __temp_296;;{ let __temp_353 = function() {
let var_23 = this; var_23._t_FnExpr = function() {};
var_23._t_Blocks = function() {};};__temp_353.prototype.getIndent= function(x) { let var_23 = this;let __temp_297;{;{ let f = x.substring(0,1);; let __temp_298 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_298.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return (f) + (var_23.getIndent(x.substring(1,(x).length)));}
let __temp_299 = new __temp_298();let __temp_300 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_300.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return "";}
let __temp_301 = new __temp_300();__temp_297 = (((f) == (" ")) || ((f) == ("\t"))?__temp_299.apply():__temp_301.apply());}}return __temp_297;}

__temp_353.prototype.indentHelper= function(x,ind,acc) { let var_23 = this;let __temp_302 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_302.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return acc;}
let __temp_303 = new __temp_302();let __temp_308 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_308.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;let __temp_304 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_304.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return var_23.indentHelper(x.substring(1,(x).length),ind,((acc) + ("\n")) + (ind));}
let __temp_305 = new __temp_304();let __temp_306 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_306.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return var_23.indentHelper(x.substring(1,(x).length),ind,(acc) + (x.substring(0,1)));}
let __temp_307 = new __temp_306();return ((x.substring(0,1)) == ("\n")?__temp_305.apply():__temp_307.apply());}
let __temp_309 = new __temp_308();return (((x).length) == (0)?__temp_303.apply():__temp_309.apply());}

__temp_353.prototype.indent= function(x,ind) { let var_23 = this;return var_23.indentHelper(x,ind,"");}

__temp_353.prototype.getToNewLine= function(x) { let var_23 = this;let __temp_310 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_310.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return "";}
let __temp_311 = new __temp_310();let __temp_316 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_316.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;let __temp_312 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_312.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return "";}
let __temp_313 = new __temp_312();let __temp_314 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_314.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return (x.substring(0,1)) + (var_23.getToNewLine(x.substring(1,(x).length)));}
let __temp_315 = new __temp_314();return ((x.substring(0,1)) == ("\n")?__temp_313.apply():__temp_315.apply());}
let __temp_317 = new __temp_316();return (((x).length) == (0)?__temp_311.apply():__temp_317.apply());}

__temp_353.prototype.indentAndMakeIf= function(x,cond) { let var_23 = this;let __temp_318;{;{ let ind = var_23.getIndent(x);; __temp_318 = (((("if") + (cond)) + ("\n")) + (ind)) + (var_23.indent(x,ind));}}return __temp_318;}

__temp_353.prototype.elifAST= function(input,ctx) { let var_23 = this;let __temp_319;{;{ let elifRegex = regex.apply("^elif\\s*\\(.*\\)\\s*(\\/\\/[^\n]*)?\n");;{ let mOpt = elifRegex.findPrefixMatchOf(input);;{ let isElif = (mOpt).isDefined;;{ let __temp_320 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_320.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return regex.makeMatch("",input);}
let __temp_321 = new __temp_320();let em = mOpt.getOrElse(__temp_321).after();;{ let __temp_322 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_322.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return "";}
let __temp_323 = new __temp_322();let __temp_324 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_324.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return var_23.getToNewLine(input.substring(4,(input).length));}
let __temp_325 = new __temp_324();let cond = (((input).length) < (4)?__temp_323.apply():__temp_325.apply());; let __temp_326 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_326.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return var_23.indentAndMakeIf(em,cond);}
let __temp_327 = new __temp_326();let __temp_328 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_328.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return em;}
let __temp_329 = new __temp_328();__temp_319 = (isElif?__temp_327.apply():__temp_329.apply());}}}}}}return __temp_319;}

__temp_353.prototype.elifOrElseAST= function(input,ctx) { let var_23 = this;let __temp_330;{;{ let elseRegex = regex.apply("^else\\s*(\\/\\/[^\n]*)?\n");;{ let mOpt = elseRegex.findPrefixMatchOf(input);;{ let __temp_331 = function() {
let dontcare = this; dontcare._t_U = function() {};};let __temp_332 = new __temp_331();let __temp_333 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_333.prototype.apply= function(x) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return x.after();}
let __temp_334 = new __temp_333();let __temp_335 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_335.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return var_23.elifAST(input,ctx);}
let __temp_336 = new __temp_335();let body = mOpt.map(__temp_332,__temp_334).getOrElse(__temp_336);; __temp_330 = var_23.toUnitOrAST(body,ctx);}}}}return __temp_330;}

__temp_353.prototype.thenBlockMatch= function(input) { let var_23 = this;let __temp_337;{;{ let blockRegex = regex.apply("(\\s[^\n]*\n)+");;{ let blockMatchOpt = blockRegex.findPrefixMatchOf(input);;{ let fullMatch = regex.makeMatch(input,"");;{ let __temp_338 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_338.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return fullMatch;}
let __temp_339 = new __temp_338();let m = blockMatchOpt.getOrElse(__temp_339);; __temp_337 = m;}}}}}return __temp_337;}

__temp_353.prototype.toUnitOrAST= function(input,ctx) { let var_23 = this;let __temp_340;{;{ let stripped = MOD_M_wyvern_DOT_internal_DOT_ast.stripLeadingWhitespace(input,false);; let __temp_341 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_341.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return MOD_M_wyvern_DOT_internal_DOT_ast.int(0);}
let __temp_342 = new __temp_341();let __temp_343 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_343.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return MOD_M_wyvern_DOT_internal_DOT_ast.parseExpression(stripped,ctx);}
let __temp_344 = new __temp_343();__temp_340 = ((input) == ("")?__temp_342.apply():__temp_344.apply());}}return __temp_340;}

__temp_353.prototype.doif= function(condition,tt,ff) { let var_23 = this;let __temp_345 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_345.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return tt.apply();}
let __temp_346 = new __temp_345();let __temp_347 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_347.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return ff.apply();}
let __temp_348 = new __temp_347();return (condition?__temp_346.apply():__temp_348.apply());}

__temp_353.prototype.doifblk= function(condition,block) { let var_23 = this;let __temp_349 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_349.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return block.evalTrue();}
let __temp_350 = new __temp_349();let __temp_351 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_351.prototype.apply= function() { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return block.evalFalse();}
let __temp_352 = new __temp_351();return (condition?__temp_350.apply():__temp_352.apply());}
let __temp_354 = new __temp_353();let var_23 = __temp_354;; let __temp_355 = function() {
let var_24 = this; var_24.regex = regex;
var_24._t_AST = var_22._t_AST
var_24._t_FnExpr = var_23._t_FnExpr
var_24._t_Blocks = var_23._t_Blocks};__temp_355.prototype.getIndent= function(x) { let var_24 = this;return var_23.getIndent(x);}

__temp_355.prototype.indentHelper= function(x,ind,acc) { let var_24 = this;return var_23.indentHelper(x,ind,acc);}

__temp_355.prototype.indent= function(x,ind) { let var_24 = this;return var_23.indent(x,ind);}

__temp_355.prototype.getToNewLine= function(x) { let var_24 = this;return var_23.getToNewLine(x);}

__temp_355.prototype.indentAndMakeIf= function(x,cond) { let var_24 = this;return var_23.indentAndMakeIf(x,cond);}

__temp_355.prototype.elifAST= function(input,ctx) { let var_24 = this;return var_23.elifAST(input,ctx);}

__temp_355.prototype.elifOrElseAST= function(input,ctx) { let var_24 = this;return var_23.elifOrElseAST(input,ctx);}

__temp_355.prototype.thenBlockMatch= function(input) { let var_24 = this;return var_23.thenBlockMatch(input);}

__temp_355.prototype.toUnitOrAST= function(input,ctx) { let var_24 = this;return var_23.toUnitOrAST(input,ctx);}

__temp_355.prototype.doif= function(condition,tt,ff) { let var_24 = this;return var_23.doif(condition,tt,ff);}

__temp_355.prototype.doifblk= function(condition,block) { let var_24 = this;return var_23.doifblk(condition,block);}
let __temp_356 = new __temp_355();__temp_294 = __temp_356;}}}}}}}}}}let MOD_M_wyvern_DOT_IfTSL = __temp_294;let __temp_357;{;{ let system = MOD_M_system;;{ let IfTSL = MOD_M_wyvern_DOT_IfTSL;;{ let runtime = MOD_M_wyvern_DOT_runtime;;{ let option = MOD_M_wyvern_DOT_option;;{ let __temp_358 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_358.prototype.apply= function(cond,tt,ff) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return MOD_M_wyvern_DOT_IfTSL.doif(cond,tt,ff);}
let __temp_359 = new __temp_358();let ifelseARG = __temp_359;;{ let __temp_360 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_360.prototype.apply= function(cond,blocks) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return MOD_M_wyvern_DOT_IfTSL.doifblk(cond,blocks);}
let __temp_361 = new __temp_360();let _if = __temp_361;;let __temp_362 = function() {
let unitSelf = this; };let __temp_363 = new __temp_362();__temp_363;{ let ast = MOD_M_wyvern_DOT_internal_DOT_ast;;{ let list = MOD_M_wyvern_DOT_internal_DOT_list;;{ let option = MOD_M_wyvern_DOT_option;;{ let IfTSL = MOD_M_wyvern_DOT_IfTSL;;{ let __temp_364 = function() {
let var_29 = this; var_29._t_List = MOD_M_wyvern_DOT_internal_DOT_list._t_List};let __temp_365 = new __temp_364();let var_29 = __temp_365;;{ let __temp_370 = function() {
let var_34 = this; };__temp_370.prototype.make= function(__generic__T) { let var_34 = this;let __temp_366 = function() {
let dontcare = this; dontcare._t_E = __generic__T._t_T};let __temp_367 = new __temp_366();return MOD_M_wyvern_DOT_internal_DOT_list.make(__temp_367);}

__temp_370.prototype.makeD= function() { let var_34 = this;let __temp_368 = function() {
let dontcare = this; dontcare._t_E = function() {};};let __temp_369 = new __temp_368();return MOD_M_wyvern_DOT_internal_DOT_list.make(__temp_369);}
let __temp_371 = new __temp_370();let var_34 = __temp_371;; let __temp_372 = function() {
let var_35 = this; var_35._t_List = var_29._t_List};__temp_372.prototype.make= function(__generic__T) { let var_35 = this;return var_34.make(__generic__T);}

__temp_372.prototype.makeD= function() { let var_35 = this;return var_34.makeD();}
let __temp_373 = new __temp_372();__temp_357 = __temp_373;}}}}}}}}}}}}}let MOD_M_wyvern_DOT_collections_DOT_list = __temp_357;let __temp_374;{;{ let system = MOD_M_system;;{ let IfTSL = MOD_M_wyvern_DOT_IfTSL;;{ let runtime = MOD_M_wyvern_DOT_runtime;;{ let option = MOD_M_wyvern_DOT_option;;{ let __temp_375 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_375.prototype.apply= function(cond,tt,ff) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return MOD_M_wyvern_DOT_IfTSL.doif(cond,tt,ff);}
let __temp_376 = new __temp_375();let ifelseARG = __temp_376;;{ let __temp_377 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_377.prototype.apply= function(cond,blocks) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return MOD_M_wyvern_DOT_IfTSL.doifblk(cond,blocks);}
let __temp_378 = new __temp_377();let _if = __temp_378;;let __temp_379 = function() {
let unitSelf = this; };let __temp_380 = new __temp_379();__temp_380;{ let ast = MOD_M_wyvern_DOT_internal_DOT_ast;;{ let list = MOD_M_wyvern_DOT_collections_DOT_list;;{ let lexing = FFI_lexing;;{ let __temp_381 = function() {
let var_36 = this; var_36._t_Lexer = function() {};};__temp_381.prototype.makeLexer= function(lexDesc) { let var_36 = this;return lexing.makeLexer(lexDesc);}
let __temp_382 = new __temp_381();let var_36 = __temp_382;; let __temp_383 = function() {
let var_37 = this; var_37._t_Lexer = var_36._t_Lexer};__temp_383.prototype.makeLexer= function(lexDesc) { let var_37 = this;return var_36.makeLexer(lexDesc);}
let __temp_384 = new __temp_383();__temp_374 = __temp_384;}}}}}}}}}}}let MOD_M_lexing = __temp_374;let __temp_385;{;{ let system = MOD_M_system;;{ let IfTSL = MOD_M_wyvern_DOT_IfTSL;;{ let runtime = MOD_M_wyvern_DOT_runtime;;{ let option = MOD_M_wyvern_DOT_option;;{ let __temp_386 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_386.prototype.apply= function(cond,tt,ff) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return MOD_M_wyvern_DOT_IfTSL.doif(cond,tt,ff);}
let __temp_387 = new __temp_386();let ifelseARG = __temp_387;;{ let __temp_388 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_388.prototype.apply= function(cond,blocks) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return MOD_M_wyvern_DOT_IfTSL.doifblk(cond,blocks);}
let __temp_389 = new __temp_388();let _if = __temp_389;;let __temp_390 = function() {
let unitSelf = this; };let __temp_391 = new __temp_390();__temp_391;{ let ast = MOD_M_wyvern_DOT_internal_DOT_ast;;{ let list = MOD_M_wyvern_DOT_collections_DOT_list;;{ let parsing = FFI_parsing;;{ let lexing = MOD_M_lexing;;{ let __temp_392 = function() {
let var_38 = this; var_38._t_Grammar = function() {};};let __temp_393 = new __temp_392();let var_38 = __temp_393;;{ let __temp_394 = function() {
let var_39 = this; var_39._t_Parser = function() {};};__temp_394.prototype.makeParser= function(grammar,lexer) { let var_39 = this;return parsing.makeParser(grammar,lexer);}
let __temp_395 = new __temp_394();let var_39 = __temp_395;; let __temp_396 = function() {
let var_40 = this; var_40._t_Grammar = var_38._t_Grammar
var_40._t_Parser = var_39._t_Parser};__temp_396.prototype.makeParser= function(grammar,lexer) { let var_40 = this;return var_39.makeParser(grammar,lexer);}
let __temp_397 = new __temp_396();__temp_385 = __temp_397;}}}}}}}}}}}}}let MOD_M_parsing = __temp_385;let __temp_398;{;{ let system = MOD_M_system;;{ let IfTSL = MOD_M_wyvern_DOT_IfTSL;;{ let runtime = MOD_M_wyvern_DOT_runtime;;{ let option = MOD_M_wyvern_DOT_option;;{ let __temp_399 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_399.prototype.apply= function(cond,tt,ff) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return MOD_M_wyvern_DOT_IfTSL.doif(cond,tt,ff);}
let __temp_400 = new __temp_399();let ifelseARG = __temp_400;;{ let __temp_401 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_401.prototype.apply= function(cond,blocks) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return MOD_M_wyvern_DOT_IfTSL.doifblk(cond,blocks);}
let __temp_402 = new __temp_401();let _if = __temp_402;;let __temp_403 = function() {
let unitSelf = this; };let __temp_404 = new __temp_403();__temp_404; { let __temp_405 = function() {
let var_25 = this; var_25._t_UnannotatedStdout = function() {};};let __temp_406 = new __temp_405();let var_25 = __temp_406;;}}}}}}}}let MOD_M_UnannotatedStdout = __temp_398;let __temp_407;{;{ let system = MOD_M_system;;{ let IfTSL = MOD_M_wyvern_DOT_IfTSL;;{ let runtime = MOD_M_wyvern_DOT_runtime;;{ let option = MOD_M_wyvern_DOT_option;;{ let __temp_408 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_408.prototype.apply= function(cond,tt,ff) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return MOD_M_wyvern_DOT_IfTSL.doif(cond,tt,ff);}
let __temp_409 = new __temp_408();let ifelseARG = __temp_409;;{ let __temp_410 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_410.prototype.apply= function(cond,blocks) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return MOD_M_wyvern_DOT_IfTSL.doifblk(cond,blocks);}
let __temp_411 = new __temp_410();let _if = __temp_411;;let __temp_412 = function() {
let unitSelf = this; };let __temp_413 = new __temp_412();__temp_413; let __temp_421 = function() {
let dontcare = this; };__temp_421.prototype.apply= function(javascript) { let dontcare = this;let __temp_414;{;{ let stdio = FFI_stdio;;{ let __temp_415 = function() {
let var_26 = this; var_26._t_Printable = function() {};};let __temp_416 = new __temp_415();let var_26 = __temp_416;;{ let __temp_417 = function() {
let var_27 = this; };__temp_417.prototype.print= function(text) { let var_27 = this;return stdio.print(text);}

__temp_417.prototype.printInt= function(n) { let var_27 = this;return stdio.printInt(n);}

__temp_417.prototype.printBoolean= function(b) { let var_27 = this;return stdio.printBoolean(b);}

__temp_417.prototype.printFloat= function(f) { let var_27 = this;return stdio.printFloat(f);}

__temp_417.prototype.println= function() { let var_27 = this;return stdio.println();}

__temp_417.prototype.flush= function() { let var_27 = this;return stdio.flush();}
let __temp_418 = new __temp_417();let var_27 = __temp_418;; let __temp_419 = function() {
let var_28 = this; var_28._t_Printable = var_26._t_Printable};__temp_419.prototype.print= function(text) { let var_28 = this;return var_27.print(text);}

__temp_419.prototype.printInt= function(n) { let var_28 = this;return var_27.printInt(n);}

__temp_419.prototype.printBoolean= function(b) { let var_28 = this;return var_27.printBoolean(b);}

__temp_419.prototype.printFloat= function(f) { let var_28 = this;return var_27.printFloat(f);}

__temp_419.prototype.println= function() { let var_28 = this;return var_27.println();}

__temp_419.prototype.flush= function() { let var_28 = this;return var_27.flush();}
let __temp_420 = new __temp_419();__temp_414 = __temp_420;}}}}return __temp_414;}
let __temp_422 = new __temp_421();__temp_407 = __temp_422;}}}}}}}let MOD_M_stdout = __temp_407;let __temp_423;{;{ let system = MOD_M_system;;{ let IfTSL = MOD_M_wyvern_DOT_IfTSL;;{ let runtime = MOD_M_wyvern_DOT_runtime;;{ let option = MOD_M_wyvern_DOT_option;;{ let __temp_424 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_424.prototype.apply= function(cond,tt,ff) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return MOD_M_wyvern_DOT_IfTSL.doif(cond,tt,ff);}
let __temp_425 = new __temp_424();let ifelseARG = __temp_425;;{ let __temp_426 = function() {
let atlambda_HYPHEN_structual_HYPHEN_decl = this; };__temp_426.prototype.apply= function(cond,blocks) { let atlambda_HYPHEN_structual_HYPHEN_decl = this;return MOD_M_wyvern_DOT_IfTSL.doifblk(cond,blocks);}
let __temp_427 = new __temp_426();let _if = __temp_427;;let __temp_428 = function() {
let unitSelf = this; };let __temp_429 = new __temp_428();__temp_429;{ let __temp_430 = function() {
let unused = this; };__temp_430.prototype = new system._t_JavaScript();let __temp_431 = new __temp_430();let javascript = __temp_431;;{ let __temp_432 = function() {
let unused = this; };__temp_432.prototype = new system._t_JavaScript();let __temp_433 = new __temp_432();let stdout = MOD_M_stdout.apply(__temp_433);;{ let js = FFI_js;;{ let lexing = MOD_M_lexing;;{ let parsing = MOD_M_parsing;;{ let lowLevelLexer = MOD_M_lexing.makeLexer("        WS:     {match: /\\s+/, lineBreaks: true},\n        number: /[0-9][0-9]*/,\n        plus:   \'+\',\n        times:  \'*\'\n");;{ let __temp_441 = function() {
let _this = this; };__temp_441.prototype.next= function() { let _this = this;let __temp_434;{;{ let token = lowLevelLexer.next();; let __temp_440;if (js.isUndefined(token)) {let __temp_435;{; __temp_435 = token;}__temp_440 = __temp_435; } else { let __temp_436;{; let __temp_439;if (js.equalsJS("WS",(token).type)) {let __temp_437;{; __temp_437 = _this.next();}__temp_439 = __temp_437; } else { let __temp_438;{; __temp_438 = token;}__temp_439 = __temp_438; }__temp_436 = __temp_439;}__temp_440 = __temp_436; }__temp_434 = __temp_440;}}return __temp_434;}

__temp_441.prototype.save= function() { let _this = this;return lowLevelLexer.save();}

__temp_441.prototype.reset= function(chunk,info) { let _this = this;return lowLevelLexer.reset(chunk,info);}

__temp_441.prototype.formatError= function(token) { let _this = this;return lowLevelLexer.formatError(token);}

__temp_441.prototype.has= function(name) { let _this = this;return lowLevelLexer.has(name);}
let __temp_442 = new __temp_441();let lexer = __temp_442;;lexer.reset("1+2",lexer.save());stdout.print((lexer.next()).text);stdout.print((lexer.next()).text);stdout.print((lexer.next()).text);stdout.println();{ let grammar = "    E -> E %plus F     {% function(d) { return d[0] + d[2] } %}\n       | F             {% id %}\n       \n    F -> F %times n    {% function(d) { return d[0] * d[2] } %}\n       | n             {% id %}\n       \n    n -> %number       {% function(d) { return parseInt(d[0].value) } %}\n";;{ let parser = MOD_M_parsing.makeParser(grammar,lexer);;parser.feed(" \t 15+12*2 \n "); __temp_423 = stdout.print((parser).results);}}}}}}}}}}}}}}}}let MOD_M_toplevel = __temp_423;})();