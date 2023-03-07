"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.MouseClick = exports.CSSInsertRuleURLBased = exports.PageClose = exports.CustomIssue = exports.TechnicalInfo = exports.SetCSSDataURLBased = exports.SetNodeAttributeURLBased = exports.LongTask = exports.SetPageVisibility = exports.ConnectionInformation = exports.ResourceTiming = exports.PerformanceTrack = exports.GraphQL = exports.NgRx = exports.MobX = exports.Vuex = exports.Redux = exports.StateAction = exports.OTable = exports.Profiler = exports.Fetch = exports.CSSDeleteRule = exports.CSSInsertRule = exports.Metadata = exports.UserAnonymousID = exports.UserID = exports.RawCustomEvent = exports.JSException = exports.PageRenderTiming = exports.PageLoadTiming = exports.ConsoleLog = exports.MouseMove = exports.SetInputChecked = exports.SetInputValue = exports.SetInputTarget = exports.SetNodeScroll = exports.SetNodeData = exports.RemoveNodeAttribute = exports.SetNodeAttribute = exports.RemoveNode = exports.MoveNode = exports.CreateTextNode = exports.CreateElementNode = exports.CreateDocument = exports.SetViewportScroll = exports.SetViewportSize = exports.SetPageLocation = exports.Timestamp = exports.BatchMeta = exports.classes = void 0;
exports.CreateIFrameDocument = void 0;
function bindNew(Class) {
    function _Class(...args) {
        return new Class(...args);
    }
    _Class.prototype = Class.prototype;
    return _Class;
}
exports.classes = new Map();
class _BatchMeta {
    constructor(pageNo, firstIndex, timestamp) {
        this.pageNo = pageNo;
        this.firstIndex = firstIndex;
        this.timestamp = timestamp;
        this._id = 80;
    }
    encode(writer) {
        return writer.uint(80) &&
            writer.uint(this.pageNo) &&
            writer.uint(this.firstIndex) &&
            writer.int(this.timestamp);
    }
}
exports.BatchMeta = bindNew(_BatchMeta);
exports.classes.set(80, exports.BatchMeta);
class _Timestamp {
    constructor(timestamp) {
        this.timestamp = timestamp;
        this._id = 0;
    }
    encode(writer) {
        return writer.uint(0) &&
            writer.uint(this.timestamp);
    }
}
exports.Timestamp = bindNew(_Timestamp);
exports.classes.set(0, exports.Timestamp);
class _SetPageLocation {
    constructor(url, referrer, navigationStart) {
        this.url = url;
        this.referrer = referrer;
        this.navigationStart = navigationStart;
        this._id = 4;
    }
    encode(writer) {
        return writer.uint(4) &&
            writer.string(this.url) &&
            writer.string(this.referrer) &&
            writer.uint(this.navigationStart);
    }
}
exports.SetPageLocation = bindNew(_SetPageLocation);
exports.classes.set(4, exports.SetPageLocation);
class _SetViewportSize {
    constructor(width, height) {
        this.width = width;
        this.height = height;
        this._id = 5;
    }
    encode(writer) {
        return writer.uint(5) &&
            writer.uint(this.width) &&
            writer.uint(this.height);
    }
}
exports.SetViewportSize = bindNew(_SetViewportSize);
exports.classes.set(5, exports.SetViewportSize);
class _SetViewportScroll {
    constructor(x, y) {
        this.x = x;
        this.y = y;
        this._id = 6;
    }
    encode(writer) {
        return writer.uint(6) &&
            writer.int(this.x) &&
            writer.int(this.y);
    }
}
exports.SetViewportScroll = bindNew(_SetViewportScroll);
exports.classes.set(6, exports.SetViewportScroll);
class _CreateDocument {
    constructor() {
        this._id = 7;
    }
    encode(writer) {
        return writer.uint(7);
    }
}
exports.CreateDocument = bindNew(_CreateDocument);
exports.classes.set(7, exports.CreateDocument);
class _CreateElementNode {
    constructor(id, parentID, index, tag, svg) {
        this.id = id;
        this.parentID = parentID;
        this.index = index;
        this.tag = tag;
        this.svg = svg;
        this._id = 8;
    }
    encode(writer) {
        return writer.uint(8) &&
            writer.uint(this.id) &&
            writer.uint(this.parentID) &&
            writer.uint(this.index) &&
            writer.string(this.tag) &&
            writer.boolean(this.svg);
    }
}
exports.CreateElementNode = bindNew(_CreateElementNode);
exports.classes.set(8, exports.CreateElementNode);
class _CreateTextNode {
    constructor(id, parentID, index) {
        this.id = id;
        this.parentID = parentID;
        this.index = index;
        this._id = 9;
    }
    encode(writer) {
        return writer.uint(9) &&
            writer.uint(this.id) &&
            writer.uint(this.parentID) &&
            writer.uint(this.index);
    }
}
exports.CreateTextNode = bindNew(_CreateTextNode);
exports.classes.set(9, exports.CreateTextNode);
class _MoveNode {
    constructor(id, parentID, index) {
        this.id = id;
        this.parentID = parentID;
        this.index = index;
        this._id = 10;
    }
    encode(writer) {
        return writer.uint(10) &&
            writer.uint(this.id) &&
            writer.uint(this.parentID) &&
            writer.uint(this.index);
    }
}
exports.MoveNode = bindNew(_MoveNode);
exports.classes.set(10, exports.MoveNode);
class _RemoveNode {
    constructor(id) {
        this.id = id;
        this._id = 11;
    }
    encode(writer) {
        return writer.uint(11) &&
            writer.uint(this.id);
    }
}
exports.RemoveNode = bindNew(_RemoveNode);
exports.classes.set(11, exports.RemoveNode);
class _SetNodeAttribute {
    constructor(id, name, value) {
        this.id = id;
        this.name = name;
        this.value = value;
        this._id = 12;
    }
    encode(writer) {
        return writer.uint(12) &&
            writer.uint(this.id) &&
            writer.string(this.name) &&
            writer.string(this.value);
    }
}
exports.SetNodeAttribute = bindNew(_SetNodeAttribute);
exports.classes.set(12, exports.SetNodeAttribute);
class _RemoveNodeAttribute {
    constructor(id, name) {
        this.id = id;
        this.name = name;
        this._id = 13;
    }
    encode(writer) {
        return writer.uint(13) &&
            writer.uint(this.id) &&
            writer.string(this.name);
    }
}
exports.RemoveNodeAttribute = bindNew(_RemoveNodeAttribute);
exports.classes.set(13, exports.RemoveNodeAttribute);
class _SetNodeData {
    constructor(id, data) {
        this.id = id;
        this.data = data;
        this._id = 14;
    }
    encode(writer) {
        return writer.uint(14) &&
            writer.uint(this.id) &&
            writer.string(this.data);
    }
}
exports.SetNodeData = bindNew(_SetNodeData);
exports.classes.set(14, exports.SetNodeData);
class _SetNodeScroll {
    constructor(id, x, y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this._id = 16;
    }
    encode(writer) {
        return writer.uint(16) &&
            writer.uint(this.id) &&
            writer.int(this.x) &&
            writer.int(this.y);
    }
}
exports.SetNodeScroll = bindNew(_SetNodeScroll);
exports.classes.set(16, exports.SetNodeScroll);
class _SetInputTarget {
    constructor(id, label) {
        this.id = id;
        this.label = label;
        this._id = 17;
    }
    encode(writer) {
        return writer.uint(17) &&
            writer.uint(this.id) &&
            writer.string(this.label);
    }
}
exports.SetInputTarget = bindNew(_SetInputTarget);
exports.classes.set(17, exports.SetInputTarget);
class _SetInputValue {
    constructor(id, value, mask) {
        this.id = id;
        this.value = value;
        this.mask = mask;
        this._id = 18;
    }
    encode(writer) {
        return writer.uint(18) &&
            writer.uint(this.id) &&
            writer.string(this.value) &&
            writer.int(this.mask);
    }
}
exports.SetInputValue = bindNew(_SetInputValue);
exports.classes.set(18, exports.SetInputValue);
class _SetInputChecked {
    constructor(id, checked) {
        this.id = id;
        this.checked = checked;
        this._id = 19;
    }
    encode(writer) {
        return writer.uint(19) &&
            writer.uint(this.id) &&
            writer.boolean(this.checked);
    }
}
exports.SetInputChecked = bindNew(_SetInputChecked);
exports.classes.set(19, exports.SetInputChecked);
class _MouseMove {
    constructor(x, y) {
        this.x = x;
        this.y = y;
        this._id = 20;
    }
    encode(writer) {
        return writer.uint(20) &&
            writer.uint(this.x) &&
            writer.uint(this.y);
    }
}
exports.MouseMove = bindNew(_MouseMove);
exports.classes.set(20, exports.MouseMove);
class _ConsoleLog {
    constructor(level, value) {
        this.level = level;
        this.value = value;
        this._id = 22;
    }
    encode(writer) {
        return writer.uint(22) &&
            writer.string(this.level) &&
            writer.string(this.value);
    }
}
exports.ConsoleLog = bindNew(_ConsoleLog);
exports.classes.set(22, exports.ConsoleLog);
class _PageLoadTiming {
    constructor(requestStart, responseStart, responseEnd, domContentLoadedEventStart, domContentLoadedEventEnd, loadEventStart, loadEventEnd, firstPaint, firstContentfulPaint) {
        this.requestStart = requestStart;
        this.responseStart = responseStart;
        this.responseEnd = responseEnd;
        this.domContentLoadedEventStart = domContentLoadedEventStart;
        this.domContentLoadedEventEnd = domContentLoadedEventEnd;
        this.loadEventStart = loadEventStart;
        this.loadEventEnd = loadEventEnd;
        this.firstPaint = firstPaint;
        this.firstContentfulPaint = firstContentfulPaint;
        this._id = 23;
    }
    encode(writer) {
        return writer.uint(23) &&
            writer.uint(this.requestStart) &&
            writer.uint(this.responseStart) &&
            writer.uint(this.responseEnd) &&
            writer.uint(this.domContentLoadedEventStart) &&
            writer.uint(this.domContentLoadedEventEnd) &&
            writer.uint(this.loadEventStart) &&
            writer.uint(this.loadEventEnd) &&
            writer.uint(this.firstPaint) &&
            writer.uint(this.firstContentfulPaint);
    }
}
exports.PageLoadTiming = bindNew(_PageLoadTiming);
exports.classes.set(23, exports.PageLoadTiming);
class _PageRenderTiming {
    constructor(speedIndex, visuallyComplete, timeToInteractive) {
        this.speedIndex = speedIndex;
        this.visuallyComplete = visuallyComplete;
        this.timeToInteractive = timeToInteractive;
        this._id = 24;
    }
    encode(writer) {
        return writer.uint(24) &&
            writer.uint(this.speedIndex) &&
            writer.uint(this.visuallyComplete) &&
            writer.uint(this.timeToInteractive);
    }
}
exports.PageRenderTiming = bindNew(_PageRenderTiming);
exports.classes.set(24, exports.PageRenderTiming);
class _JSException {
    constructor(name, message, payload) {
        this.name = name;
        this.message = message;
        this.payload = payload;
        this._id = 25;
    }
    encode(writer) {
        return writer.uint(25) &&
            writer.string(this.name) &&
            writer.string(this.message) &&
            writer.string(this.payload);
    }
}
exports.JSException = bindNew(_JSException);
exports.classes.set(25, exports.JSException);
class _RawCustomEvent {
    constructor(name, payload) {
        this.name = name;
        this.payload = payload;
        this._id = 27;
    }
    encode(writer) {
        return writer.uint(27) &&
            writer.string(this.name) &&
            writer.string(this.payload);
    }
}
exports.RawCustomEvent = bindNew(_RawCustomEvent);
exports.classes.set(27, exports.RawCustomEvent);
class _UserID {
    constructor(id) {
        this.id = id;
        this._id = 28;
    }
    encode(writer) {
        return writer.uint(28) &&
            writer.string(this.id);
    }
}
exports.UserID = bindNew(_UserID);
exports.classes.set(28, exports.UserID);
class _UserAnonymousID {
    constructor(id) {
        this.id = id;
        this._id = 29;
    }
    encode(writer) {
        return writer.uint(29) &&
            writer.string(this.id);
    }
}
exports.UserAnonymousID = bindNew(_UserAnonymousID);
exports.classes.set(29, exports.UserAnonymousID);
class _Metadata {
    constructor(key, value) {
        this.key = key;
        this.value = value;
        this._id = 30;
    }
    encode(writer) {
        return writer.uint(30) &&
            writer.string(this.key) &&
            writer.string(this.value);
    }
}
exports.Metadata = bindNew(_Metadata);
exports.classes.set(30, exports.Metadata);
class _CSSInsertRule {
    constructor(id, rule, index) {
        this.id = id;
        this.rule = rule;
        this.index = index;
        this._id = 37;
    }
    encode(writer) {
        return writer.uint(37) &&
            writer.uint(this.id) &&
            writer.string(this.rule) &&
            writer.uint(this.index);
    }
}
exports.CSSInsertRule = bindNew(_CSSInsertRule);
exports.classes.set(37, exports.CSSInsertRule);
class _CSSDeleteRule {
    constructor(id, index) {
        this.id = id;
        this.index = index;
        this._id = 38;
    }
    encode(writer) {
        return writer.uint(38) &&
            writer.uint(this.id) &&
            writer.uint(this.index);
    }
}
exports.CSSDeleteRule = bindNew(_CSSDeleteRule);
exports.classes.set(38, exports.CSSDeleteRule);
class _Fetch {
    constructor(method, url, request, response, status, timestamp, duration) {
        this.method = method;
        this.url = url;
        this.request = request;
        this.response = response;
        this.status = status;
        this.timestamp = timestamp;
        this.duration = duration;
        this._id = 39;
    }
    encode(writer) {
        return writer.uint(39) &&
            writer.string(this.method) &&
            writer.string(this.url) &&
            writer.string(this.request) &&
            writer.string(this.response) &&
            writer.uint(this.status) &&
            writer.uint(this.timestamp) &&
            writer.uint(this.duration);
    }
}
exports.Fetch = bindNew(_Fetch);
exports.classes.set(39, exports.Fetch);
class _Profiler {
    constructor(name, duration, args, result) {
        this.name = name;
        this.duration = duration;
        this.args = args;
        this.result = result;
        this._id = 40;
    }
    encode(writer) {
        return writer.uint(40) &&
            writer.string(this.name) &&
            writer.uint(this.duration) &&
            writer.string(this.args) &&
            writer.string(this.result);
    }
}
exports.Profiler = bindNew(_Profiler);
exports.classes.set(40, exports.Profiler);
class _OTable {
    constructor(key, value) {
        this.key = key;
        this.value = value;
        this._id = 41;
    }
    encode(writer) {
        return writer.uint(41) &&
            writer.string(this.key) &&
            writer.string(this.value);
    }
}
exports.OTable = bindNew(_OTable);
exports.classes.set(41, exports.OTable);
class _StateAction {
    constructor(type) {
        this.type = type;
        this._id = 42;
    }
    encode(writer) {
        return writer.uint(42) &&
            writer.string(this.type);
    }
}
exports.StateAction = bindNew(_StateAction);
exports.classes.set(42, exports.StateAction);
class _Redux {
    constructor(action, state, duration) {
        this.action = action;
        this.state = state;
        this.duration = duration;
        this._id = 44;
    }
    encode(writer) {
        return writer.uint(44) &&
            writer.string(this.action) &&
            writer.string(this.state) &&
            writer.uint(this.duration);
    }
}
exports.Redux = bindNew(_Redux);
exports.classes.set(44, exports.Redux);
class _Vuex {
    constructor(mutation, state) {
        this.mutation = mutation;
        this.state = state;
        this._id = 45;
    }
    encode(writer) {
        return writer.uint(45) &&
            writer.string(this.mutation) &&
            writer.string(this.state);
    }
}
exports.Vuex = bindNew(_Vuex);
exports.classes.set(45, exports.Vuex);
class _MobX {
    constructor(type, payload) {
        this.type = type;
        this.payload = payload;
        this._id = 46;
    }
    encode(writer) {
        return writer.uint(46) &&
            writer.string(this.type) &&
            writer.string(this.payload);
    }
}
exports.MobX = bindNew(_MobX);
exports.classes.set(46, exports.MobX);
class _NgRx {
    constructor(action, state, duration) {
        this.action = action;
        this.state = state;
        this.duration = duration;
        this._id = 47;
    }
    encode(writer) {
        return writer.uint(47) &&
            writer.string(this.action) &&
            writer.string(this.state) &&
            writer.uint(this.duration);
    }
}
exports.NgRx = bindNew(_NgRx);
exports.classes.set(47, exports.NgRx);
class _GraphQL {
    constructor(operationKind, operationName, variables, response) {
        this.operationKind = operationKind;
        this.operationName = operationName;
        this.variables = variables;
        this.response = response;
        this._id = 48;
    }
    encode(writer) {
        return writer.uint(48) &&
            writer.string(this.operationKind) &&
            writer.string(this.operationName) &&
            writer.string(this.variables) &&
            writer.string(this.response);
    }
}
exports.GraphQL = bindNew(_GraphQL);
exports.classes.set(48, exports.GraphQL);
class _PerformanceTrack {
    constructor(frames, ticks, totalJSHeapSize, usedJSHeapSize) {
        this.frames = frames;
        this.ticks = ticks;
        this.totalJSHeapSize = totalJSHeapSize;
        this.usedJSHeapSize = usedJSHeapSize;
        this._id = 49;
    }
    encode(writer) {
        return writer.uint(49) &&
            writer.int(this.frames) &&
            writer.int(this.ticks) &&
            writer.uint(this.totalJSHeapSize) &&
            writer.uint(this.usedJSHeapSize);
    }
}
exports.PerformanceTrack = bindNew(_PerformanceTrack);
exports.classes.set(49, exports.PerformanceTrack);
class _ResourceTiming {
    constructor(timestamp, duration, ttfb, headerSize, encodedBodySize, decodedBodySize, url, initiator) {
        this.timestamp = timestamp;
        this.duration = duration;
        this.ttfb = ttfb;
        this.headerSize = headerSize;
        this.encodedBodySize = encodedBodySize;
        this.decodedBodySize = decodedBodySize;
        this.url = url;
        this.initiator = initiator;
        this._id = 53;
    }
    encode(writer) {
        return writer.uint(53) &&
            writer.uint(this.timestamp) &&
            writer.uint(this.duration) &&
            writer.uint(this.ttfb) &&
            writer.uint(this.headerSize) &&
            writer.uint(this.encodedBodySize) &&
            writer.uint(this.decodedBodySize) &&
            writer.string(this.url) &&
            writer.string(this.initiator);
    }
}
exports.ResourceTiming = bindNew(_ResourceTiming);
exports.classes.set(53, exports.ResourceTiming);
class _ConnectionInformation {
    constructor(downlink, type) {
        this.downlink = downlink;
        this.type = type;
        this._id = 54;
    }
    encode(writer) {
        return writer.uint(54) &&
            writer.uint(this.downlink) &&
            writer.string(this.type);
    }
}
exports.ConnectionInformation = bindNew(_ConnectionInformation);
exports.classes.set(54, exports.ConnectionInformation);
class _SetPageVisibility {
    constructor(hidden) {
        this.hidden = hidden;
        this._id = 55;
    }
    encode(writer) {
        return writer.uint(55) &&
            writer.boolean(this.hidden);
    }
}
exports.SetPageVisibility = bindNew(_SetPageVisibility);
exports.classes.set(55, exports.SetPageVisibility);
class _LongTask {
    constructor(timestamp, duration, context, containerType, containerSrc, containerId, containerName) {
        this.timestamp = timestamp;
        this.duration = duration;
        this.context = context;
        this.containerType = containerType;
        this.containerSrc = containerSrc;
        this.containerId = containerId;
        this.containerName = containerName;
        this._id = 59;
    }
    encode(writer) {
        return writer.uint(59) &&
            writer.uint(this.timestamp) &&
            writer.uint(this.duration) &&
            writer.uint(this.context) &&
            writer.uint(this.containerType) &&
            writer.string(this.containerSrc) &&
            writer.string(this.containerId) &&
            writer.string(this.containerName);
    }
}
exports.LongTask = bindNew(_LongTask);
exports.classes.set(59, exports.LongTask);
class _SetNodeAttributeURLBased {
    constructor(id, name, value, baseURL) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.baseURL = baseURL;
        this._id = 60;
    }
    encode(writer) {
        return writer.uint(60) &&
            writer.uint(this.id) &&
            writer.string(this.name) &&
            writer.string(this.value) &&
            writer.string(this.baseURL);
    }
}
exports.SetNodeAttributeURLBased = bindNew(_SetNodeAttributeURLBased);
exports.classes.set(60, exports.SetNodeAttributeURLBased);
class _SetCSSDataURLBased {
    constructor(id, data, baseURL) {
        this.id = id;
        this.data = data;
        this.baseURL = baseURL;
        this._id = 61;
    }
    encode(writer) {
        return writer.uint(61) &&
            writer.uint(this.id) &&
            writer.string(this.data) &&
            writer.string(this.baseURL);
    }
}
exports.SetCSSDataURLBased = bindNew(_SetCSSDataURLBased);
exports.classes.set(61, exports.SetCSSDataURLBased);
class _TechnicalInfo {
    constructor(type, value) {
        this.type = type;
        this.value = value;
        this._id = 63;
    }
    encode(writer) {
        return writer.uint(63) &&
            writer.string(this.type) &&
            writer.string(this.value);
    }
}
exports.TechnicalInfo = bindNew(_TechnicalInfo);
exports.classes.set(63, exports.TechnicalInfo);
class _CustomIssue {
    constructor(name, payload) {
        this.name = name;
        this.payload = payload;
        this._id = 64;
    }
    encode(writer) {
        return writer.uint(64) &&
            writer.string(this.name) &&
            writer.string(this.payload);
    }
}
exports.CustomIssue = bindNew(_CustomIssue);
exports.classes.set(64, exports.CustomIssue);
class _PageClose {
    constructor() {
        this._id = 65;
    }
    encode(writer) {
        return writer.uint(65);
    }
}
exports.PageClose = bindNew(_PageClose);
exports.classes.set(65, exports.PageClose);
class _CSSInsertRuleURLBased {
    constructor(id, rule, index, baseURL) {
        this.id = id;
        this.rule = rule;
        this.index = index;
        this.baseURL = baseURL;
        this._id = 67;
    }
    encode(writer) {
        return writer.uint(67) &&
            writer.uint(this.id) &&
            writer.string(this.rule) &&
            writer.uint(this.index) &&
            writer.string(this.baseURL);
    }
}
exports.CSSInsertRuleURLBased = bindNew(_CSSInsertRuleURLBased);
exports.classes.set(67, exports.CSSInsertRuleURLBased);
class _MouseClick {
    constructor(id, hesitationTime, label, selector) {
        this.id = id;
        this.hesitationTime = hesitationTime;
        this.label = label;
        this.selector = selector;
        this._id = 69;
    }
    encode(writer) {
        return writer.uint(69) &&
            writer.uint(this.id) &&
            writer.uint(this.hesitationTime) &&
            writer.string(this.label) &&
            writer.string(this.selector);
    }
}
exports.MouseClick = bindNew(_MouseClick);
exports.classes.set(69, exports.MouseClick);
class _CreateIFrameDocument {
    constructor(frameID, id) {
        this.frameID = frameID;
        this.id = id;
        this._id = 70;
    }
    encode(writer) {
        return writer.uint(70) &&
            writer.uint(this.frameID) &&
            writer.uint(this.id);
    }
}
exports.CreateIFrameDocument = bindNew(_CreateIFrameDocument);
exports.classes.set(70, exports.CreateIFrameDocument);
