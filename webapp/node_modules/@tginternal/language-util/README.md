# Language util

## Suggesting

To get language data suggestions by user input:

```typescript
import { suggest } from '@tginternal/language-util';

console.log(suggest('cs'));
```

```typescript
[
    {
        englishName: 'Czech',
        flags: ['🇨🇿'],
        languageId: 'cs',
        originalName: 'čeština',
    },
    {
        englishName: "Czech (Czechia)",
        flags: ["🇨🇿"],
        languageId: "cs-CZ",
        originalName: "čeština (Česko)"
    },
    {
        englishName: "English",
        flags: (89) ["🇬🇧", "🇺🇸", ...],
        languageId: "en",
        originalName: "English"
    }
    ...
];
```

## Validation

```
import { isValidLanguageTag } from '@tginternal/language-util';

isValidLanguageTag("cs-CZ") //true
isValidLanguageTag("cs_CZ") //false
```
