export const LogLevel = {
    Verbose: 5,
    Log: 4,
    Warnings: 3,
    Errors: 2,
    Silent: 0,
};
function IsCustomLevel(l) {
    return typeof l === 'object';
}
export default class Logger {
    constructor(options = LogLevel.Silent) {
        this.options = options === true
            ? { level: LogLevel.Verbose }
            : typeof options === "number" ? { level: options } : options;
    }
    log(...args) {
        if (IsCustomLevel(this.options.level)
            ? this.options.level.log
            : this.options.level >= LogLevel.Log) {
            console.log(...args);
        }
    }
    warn(...args) {
        if (IsCustomLevel(this.options.level)
            ? this.options.level.warn
            : this.options.level >= LogLevel.Warnings) {
            console.warn(...args);
        }
    }
    error(...args) {
        if (IsCustomLevel(this.options.level)
            ? this.options.level.error
            : this.options.level >= LogLevel.Errors) {
            console.error(...args);
        }
    }
}
