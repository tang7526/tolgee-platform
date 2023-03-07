export declare const LogLevel: {
    readonly Verbose: 5;
    readonly Log: 4;
    readonly Warnings: 3;
    readonly Errors: 2;
    readonly Silent: 0;
};
declare type LogLevel = typeof LogLevel[keyof typeof LogLevel];
declare type CustomLevel = {
    error: boolean;
    warn: boolean;
    log: boolean;
};
interface _Options {
    level: LogLevel | CustomLevel;
    messages?: number[];
}
export declare type Options = true | _Options | LogLevel;
export default class Logger {
    private readonly options;
    constructor(options?: Options);
    log(...args: any): void;
    warn(...args: any): void;
    error(...args: any): void;
}
export {};
