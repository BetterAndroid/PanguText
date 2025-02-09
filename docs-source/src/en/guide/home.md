# Introduce

> `PanguText` is a solution for CJK (Chinese, Japanese, Korean) and English word, half-width number spacing.

## Background

This project was created because, until now, there hasn’t been a public solution to perfectly address the typography issues between Chinese, Japanese, Korean, and English.
Typically, when mixing CJK (i.e. Chinese, Japanese, Korean) with English, aesthetic issues can arise—a historical legacy stemming from the differences in writing conventions between full-width and half-width characters. Although the W3C has now established CJK typography guidelines, only a few individuals or companies willing to adhere to these standards have adopted this approach.

Currently, the known vendor solutions are as follows:

- Apple platforms (iOS, iPadOS, macOS, tvOS, watchOS) text typography solutions
- Xiaomi’s (HyperOS) text typography optimization
- OrginOS’s font-based text typography optimization

However, these solutions are closed and cannot be implemented on other platforms.
We aim to provide an open-source solution adaptable to various scenarios, featuring low intrusiveness and easy integration, allowing more developers to effectively address text typography issues.

The primary inspiration for this project comes from [pangu.js](https://github.com/vinta/pangu.js), which offers a set of regular expressions for CJK typography.
We have optimized these solutions to format text across platforms without inserting extra space characters. We extend this approach further to explore additional possibilities.

Heartfelt thanks to the original developer of **pangu.js** for providing the foundational solution.

## Effects

As you can see, the typography scheme of `PanguText` does not work by simply inserting spaces between CJK characters and English words.
Instead, it leverages each platform's native handling to automatically add whitespace between these characters, ensuring minimal intrusion.

> Before Applying (Top) vs. After Applying (Bottom)

<img src="/images/demo_01.png" width="300" />

> Dynamic Application

<img src="/images/demo_02.gif" width="480" />

`PanguText` supports dynamic application, which means it can add whitespace gaps to each character on-the-fly as you input text.

::: tip Developer's Perspective

I personally do not recommend manually inserting spaces between CJK and English characters for typographic refinement if your software or system natively supports enhanced typographic formatting. 

The spacing can vary across fonts, which may lead to formatting issues and the insertion of undesired space characters.

In certain contexts, such as URLs, filenames, or hashtags containing “#”, these spaces are not acceptable.

However, in special scenarios—for example, within code comments or documentation—it can be beneficial to add spaces, as these areas typically do not employ automated formatting tools.

Another point to consider is the use of different punctuation marks in different languages.
Avoid mixing full-width and half-width punctuation marks.
If you must use half-width punctuation marks to annotate full-width text, ensure that the half-width marks are followed by a space to complete the character space (the same applies to English).

:::

## Language Requirement

It is recommended to use Kotlin as the preferred development language.

This project is entirely written in Kotlin and is compatible with Java in some parts, but it may not be fully compatible.

All demo & sample codes in the document will be described using Kotlin, if you don’t know how to use Kotlin at all, you may not get the best experience.

## Contribution

The maintenance of this project is inseparable from the support and contributions of all developers.

This project is currently in its early stages, and there may still be some problems or lack of functions you need.

If possible, feel free to submit a PR to contribute features you think are needed to this project or goto [GitHub Issues](repo://issues)
to make suggestions to us.