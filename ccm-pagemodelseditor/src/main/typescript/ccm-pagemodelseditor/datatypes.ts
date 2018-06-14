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
    publicationStatus: PublicationStatus;
    lastPublished: number;
}

enum PageModelVersion {

    DRAFT,
    LIVE,
}

enum PublicationStatus {

    NOT_PUBLISHED,
    PUBLISHED,
    NEEDS_UPDATE,
}