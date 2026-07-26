type Locale = 'en' | 'zh-cn';

interface PageLinkRefs {
    dev: Record<string, string>[];
    prod: Record<string, string>[];
}

interface NavigationLink {
    path: string;
    title: Record<Locale, string>;
}

interface NavigationSection {
    title: Record<Locale, string>;
    links: NavigationLink[];
}

const navigationSections: NavigationSection[] = [{
    title: { en: 'Get Started', 'zh-cn': '入门' },
    links: [
        { path: '/guide/home', title: { en: 'Introduction', 'zh-cn': '介绍' } },
        { path: '/guide/quick-start', title: { en: 'Quick Start', 'zh-cn': '快速开始' } }
    ]
}, {
    title: { en: 'Libraries', 'zh-cn': '依赖' },
    links: [
        { path: '/library/android', title: { en: 'Android', 'zh-cn': 'Android' } },
        { path: '/library/compose', title: { en: 'Jetpack Compose', 'zh-cn': 'Jetpack Compose' } }
    ]
}, {
    title: { en: 'Configs', 'zh-cn': '配置' },
    links: [
        { path: '/config/r8-proguard', title: { en: 'R8 & Proguard Obfuscate', 'zh-cn': 'R8 与 Proguard 混淆' } }
    ]
}, {
    title: { en: 'About', 'zh-cn': '关于' },
    links: [
        { path: '/about/changelog', title: { en: 'Changelog', 'zh-cn': '更新日志' } },
        { path: '/about/future', title: { en: 'Looking Toward the Future', 'zh-cn': '展望未来' } },
        { path: '/about/contacts', title: { en: 'Contact Us', 'zh-cn': '联系我们' } },
        { path: '/about/about', title: { en: 'About This Document', 'zh-cn': '关于此文档' } }
    ]
}];

const topNavigationLinks: NavigationLink[] = [
    { path: '/', title: { en: 'Home', 'zh-cn': '首页' } },
    { path: '/guide/quick-start', title: { en: 'Quick Start', 'zh-cn': '快速开始' } },
    { path: '/about/changelog', title: { en: 'Changelog', 'zh-cn': '更新日志' } },
    { path: '/about/contacts', title: { en: 'Contact Us', 'zh-cn': '联系我们' } }
];

const localizedLink = (link: NavigationLink, locale: Locale) => ({
    text: link.title[locale],
    link: `/${locale}${link.path}`
});

/** Creates the VitePress navigation and sidebar for the requested locale. */
export const createThemeNavigation = (locale: Locale) => {
    const sections = navigationSections.map((section) => ({
        text: section.title[locale],
        items: section.links.map((link) => localizedLink(link, locale))
    }));
    return {
        nav: topNavigationLinks.map((link) => localizedLink(link, locale)),
        sidebar: {
            [`/${locale}/`]: sections.map((section) => ({
                text: section.text,
                collapsed: false,
                items: section.items
            }))
        }
    };
};

/** Defines shared site, development server, and repository settings. */
export const configs = {
    dev: {
        dest: '../dist',
        port: 9000
    },
    website: {
        base: '/PanguText/',
        icon: '/PanguText/images/logo.png',
        logo: '/images/logo.png',
        title: 'Pangu Text',
        locales: {
            en: {
                lang: 'en-US',
                description: 'A typographic solution for the optimal alignment of CJK characters, English words, and half-width digits'
            },
            'zh-cn': {
                lang: 'zh-CN',
                description: '一个中日韩 (CJK) 与英文单词、半角数字排版的解决方案'
            }
        }
    },
    github: {
        repo: 'https://github.com/BetterAndroid/PanguText',
        page: 'https://betterandroid.github.io/PanguText',
        branch: 'main',
        dir: 'docs-source/src'
    }
};

/** Defines custom Markdown link protocol replacements for each build mode. */
export const pageLinkRefs: PageLinkRefs = {
    dev: [
        { 'repo://': `${configs.github.repo}/` },
        // KDoc URL for local debugging, non-fixed value, adjust according to your own needs.
        // You can run ./build-dokka.sh and start the local server in dist/KDoc.
        { 'kdoc://': 'http://localhost:9001/' }
    ],
    prod: [
        { 'repo://': `${configs.github.repo}/` },
        { 'kdoc://': `${configs.github.page}/KDoc/` }
    ]
};