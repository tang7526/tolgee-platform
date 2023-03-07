"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.LogLevel = void 0;
exports.LogLevel = {
    Verbose: 5,
    Log: 4,
    Warnings: 3,
    Errors: 2,
    Silent: 0,
};
function IsCustomLevel(l) {
    return typeof l === 'object';
}
class Logger {
    constructor(options = exports.LogLevel.Silent) {
        this.options = options === true
            ? { level: exports.LogLevel.Verbose }
            : typeof options === "number" ? { level: options } : options;
    }
    log(...args) {
        if (IsCustomLevel(this.options.level)
            ? this.options.level.log
            : this.options.level >= exports.LogLevel.Log) {
            console.log(...args);
        }
    }
    warn(...args) {
        if (IsCustomLevel(this.options.level)
            ? this.options.level.warn
            : this.options.level >= exports.LogLevel.Warnings) {
            console.warn(...args);
        }
    }
    error(...args) {
        if (IsCustomLevel(this.options.level)
            ? this.options.level.error
            : this.options.level >= exports.LogLevel.Errors) {
            console.error(...args);
        }
    }
}
exports.default = Logger;
