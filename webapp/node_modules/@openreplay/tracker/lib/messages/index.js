function bindNew(Class) {
    function _Class(...args) {
        return new Class(...args);
    }
    _Class.prototype = Class.prototype;
    return _Class;
}
export const classes = new Map();
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
export const BatchMeta = bindNew(_BatchMeta);
classes.set(80, BatchMeta);
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
export const Timestamp = bindNew(_Timestamp);
classes.set(0, Timestamp);
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
export const SetPageLocation = bindNew(_SetPageLocation);
classes.set(4, SetPageLocation);
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
export const SetViewportSize = bindNew(_SetViewportSize);
classes.set(5, SetViewportSize);
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
export const SetViewportScroll = bindNew(_SetViewportScroll);
classes.set(6, SetViewportScroll);
class _CreateDocument {
    constructor() {
        this._id = 7;
    }
    encode(writer) {
        return writer.uint(7);
    }
}
export const CreateDocument = bindNew(_CreateDocument);
classes.set(7, CreateDocument);
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
export const CreateElementNode = bindNew(_CreateElementNode);
classes.set(8, CreateElementNode);
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
export const CreateTextNode = bindNew(_CreateTextNode);
classes.set(9, CreateTextNode);
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
export const MoveNode = bindNew(_MoveNode);
classes.set(10, MoveNode);
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
export const RemoveNode = bindNew(_RemoveNode);
classes.set(11, RemoveNode);
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
export const SetNodeAttribute = bindNew(_SetNodeAttribute);
classes.set(12, SetNodeAttribute);
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
export const RemoveNodeAttribute = bindNew(_RemoveNodeAttribute);
classes.set(13, RemoveNodeAttribute);
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
export const SetNodeData = bindNew(_SetNodeData);
classes.set(14, SetNodeData);
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
export const SetNodeScroll = bindNew(_SetNodeScroll);
classes.set(16, SetNodeScroll);
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
export const SetInputTarget = bindNew(_SetInputTarget);
classes.set(17, SetInputTarget);
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
export const SetInputValue = bindNew(_SetInputValue);
classes.set(18, SetInputValue);
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
export const SetInputChecked = bindNew(_SetInputChecked);
classes.set(19, SetInputChecked);
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
export const MouseMove = bindNew(_MouseMove);
classes.set(20, MouseMove);
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
export const ConsoleLog = bindNew(_ConsoleLog);
classes.set(22, ConsoleLog);
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
export const PageLoadTiming = bindNew(_PageLoadTiming);
classes.set(23, PageLoadTiming);
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
export const PageRenderTiming = bindNew(_PageRenderTiming);
classes.set(24, PageRenderTiming);
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
export const JSException = bindNew(_JSException);
classes.set(25, JSException);
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
export const RawCustomEvent = bindNew(_RawCustomEvent);
classes.set(27, RawCustomEvent);
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
export const UserID = bindNew(_UserID);
classes.set(28, UserID);
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
export const UserAnonymousID = bindNew(_UserAnonymousID);
classes.set(29, UserAnonymousID);
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
export const Metadata = bindNew(_Metadata);
classes.set(30, Metadata);
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
export const CSSInsertRule = bindNew(_CSSInsertRule);
classes.set(37, CSSInsertRule);
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
export const CSSDeleteRule = bindNew(_CSSDeleteRule);
classes.set(38, CSSDeleteRule);
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
export const Fetch = bindNew(_Fetch);
classes.set(39, Fetch);
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
export const Profiler = bindNew(_Profiler);
classes.set(40, Profiler);
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
export const OTable = bindNew(_OTable);
classes.set(41, OTable);
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
export const StateAction = bindNew(_StateAction);
classes.set(42, StateAction);
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
export const Redux = bindNew(_Redux);
classes.set(44, Redux);
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
export const Vuex = bindNew(_Vuex);
classes.set(45, Vuex);
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
export const MobX = bindNew(_MobX);
classes.set(46, MobX);
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
export const NgRx = bindNew(_NgRx);
classes.set(47, NgRx);
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
export const GraphQL = bindNew(_GraphQL);
classes.set(48, GraphQL);
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
export const PerformanceTrack = bindNew(_PerformanceTrack);
classes.set(49, PerformanceTrack);
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
export const ResourceTiming = bindNew(_ResourceTiming);
classes.set(53, ResourceTiming);
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
export const ConnectionInformation = bindNew(_ConnectionInformation);
classes.set(54, ConnectionInformation);
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
export const SetPageVisibility = bindNew(_SetPageVisibility);
classes.set(55, SetPageVisibility);
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
export const LongTask = bindNew(_LongTask);
classes.set(59, LongTask);
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
export const SetNodeAttributeURLBased = bindNew(_SetNodeAttributeURLBased);
classes.set(60, SetNodeAttributeURLBased);
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
export const SetCSSDataURLBased = bindNew(_SetCSSDataURLBased);
classes.set(61, SetCSSDataURLBased);
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
export const TechnicalInfo = bindNew(_TechnicalInfo);
classes.set(63, TechnicalInfo);
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
export const CustomIssue = bindNew(_CustomIssue);
classes.set(64, CustomIssue);
class _PageClose {
    constructor() {
        this._id = 65;
    }
    encode(writer) {
        return writer.uint(65);
    }
}
export const PageClose = bindNew(_PageClose);
classes.set(65, PageClose);
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
export const CSSInsertRuleURLBased = bindNew(_CSSInsertRuleURLBased);
classes.set(67, CSSInsertRuleURLBased);
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
export const MouseClick = bindNew(_MouseClick);
classes.set(69, MouseClick);
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
export const CreateIFrameDocument = bindNew(_CreateIFrameDocument);
classes.set(70, CreateIFrameDocument);
