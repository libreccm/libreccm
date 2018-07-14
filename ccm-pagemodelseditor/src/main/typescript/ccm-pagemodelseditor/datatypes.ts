export {
    ComponentModel,
    ContainerModel,
    PageModel,
    PageModelVersion,
    PublicationStatus,
};

interface ComponentModel {

    componentModelId: number;
    idAttribute: string;
    classAttribute: string;
    key: string;
    modelUuid: string;
    styleAttribute: string;
    type: string;
    uuid: string;
}

interface ContainerModel {

    containerUuid: string;
    key: string;
    uuid: string;
}

interface PageModel {

    containers: ContainerModel[];
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
