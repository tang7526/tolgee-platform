import App from "./index.js";
declare type Callback = () => void;
export default class Ticker {
    private readonly app;
    private timer;
    private readonly callbacks;
    constructor(app: App);
    attach(callback: Callback, n?: number, useSafe?: boolean, thisArg?: any): void;
    start(): void;
    stop(): void;
}
export {};
