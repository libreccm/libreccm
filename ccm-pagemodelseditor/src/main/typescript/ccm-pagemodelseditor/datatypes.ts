export { PageModel, PageModelVersion };

interface PageModel {

    description: string;
    modelUuid: string;
    name: string;
    pageModelId: number;
    title: string;
    type: string;
    uuid: string;
    version: PageModelVersion;
}

enum PageModelVersion {

    DRAFT,
    LIVE,
}
