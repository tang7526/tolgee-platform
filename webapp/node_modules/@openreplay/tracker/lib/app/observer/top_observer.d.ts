import Observer from "./observer.js";
import App from "../index.js";
export interface Options {
    captureIFrames: boolean;
}
export default class TopObserver extends Observer {
    private readonly options;
    constructor(app: App, options: Partial<Options>);
    private iframeObservers;
    private handleIframe;
    private shadowRootObservers;
    private handleShadowRoot;
    observe(): void;
    disconnect(): void;
}
