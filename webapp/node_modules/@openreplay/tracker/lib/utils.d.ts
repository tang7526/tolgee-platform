export declare function timestamp(): number;
export declare const stars: (str: string) => string;
export declare function normSpaces(str: string): string;
export declare function isURL(s: string): boolean;
export declare const IN_BROWSER: boolean;
export declare const DOCS_HOST = "https://docs.openreplay.com";
export declare function deprecationWarn(nameOfFeature: string, useInstead: string, docsPath?: string): void;
export declare function getLabelAttribute(e: Element): string | null;
export declare function hasOpenreplayAttribute(e: Element, name: string): boolean;
