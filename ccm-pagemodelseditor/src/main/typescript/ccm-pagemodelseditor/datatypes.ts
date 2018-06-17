export { PageModel, PageModelVersion, PublicationStatus };

interface PageModel {

    description: string;
    modelUuid: string;
    name: string;
    pageModelId: number;
    title: string;
    type: string;
    uuid: string;
    version: PageModelVersion;
    publicationStatus: string;
    lastModified: number;
    lastPublished: number;
}

enum PageModelVersion {

    DRAFT,
    LIVE,
}

enum PublicationStatus {

    NOT_PUBLISHED = "NOT_PUBLISHED",
    PUBLISHED = "PUBLISHED",
    NEEDS_UPDATE = "NEEDS_UPDATE",
}