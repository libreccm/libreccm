import * as React from "react";
import * as ReactModal from "react-modal";
import {
    ComponentModel,
    ContainerModel,
    PageModel,
    PageModelVersion,
    PublicationStatus,
} from "./datatypes";

export {
    AbstractComponentModelEditor,
    ComponentModel,
    ComponentModelEditorProps,
    ComponentModelEditorState,
    ComponentInfo,
    PageModelEditor,
    PageModelEditorState,
    // PageModelEditorProps,
    // PageModelEditorState,
};

/**
 * To render the PageModelEditor create a Typescript file with the following
 * minimal content:
 *
 * import * as React from "react";
 * import { render } from "react-dom";
 *
 * import { PageModelEditor } from "./PageModelsEditor";
 *
 * render(
 *     <PageModelEditor />,
 *     document.getElementById("cms-content"),
 * );
 */

// interface PageModelEditorContext {
//
//     pageModelSelected: boolean;
//     selectedPageModel: PageModel;
// }

// const newPageModel: PageModel = {
//     description: "New PageModel",
//     modelUuid: "",
//     name: "newPageModel",
//     pageModelId: 0,
//     title: "A new PageModel",
//     type: "",
//     uuid: "",
//     version: PageModelVersion.DRAFT,
// };

// const pageModelEditorContext: React.Context<PageModelEditorContext>
//     = React.createContext({
//         pageModelSelected: false,
//         selectedPageModel: newPageModel,
//     });

interface PageModelsListProps {

    ccmApplication: string;
    dispatcherPrefix: string;
    pageModels: PageModel[];
    selectPageModel: (selectedPageModel: PageModel) => void;
}

// interface PageModelsListState {
//
//     errorMsg: string | null;
//     pageModels: PageModel[];
// }

class PageModelsList
    extends React.Component<PageModelsListProps, {}> {

    constructor(props: PageModelsListProps) {
        super(props);

        // this.state = {
        //     errorMsg: null,
        //     pageModels: [],
        // };
    }

    // public componentDidMount() {
    //
    //     const init: RequestInit = {
    //         credentials: "same-origin",
    //         method: "GET",
    //     };
    //
    //     const url: string = `${this.props.dispatcherPrefix}`
    //         + `/page-models/${this.props.ccmApplication}`;
    //
    //     fetch(url, init)
    //         .then((response: Response) => {
    //             if (response.ok) {
    //                 response
    //                     .json()
    //                     .then((pageModels: PageModel[]) => {
    //                         this.setState({
    //                             ...this.state,
    //                             pageModels,
    //                         });
    //                     })
    //                     .catch((error) => {
    //                         this.setState({
    //                             ...this.state,
    //                             errorMsg: `Failed to retrieve PageModels `
    //                                 + ` from ${url}: ${error.message}`,
    //                         });
    //                     });
    //             } else {
    //                 this.setState({
    //                     ...this.state,
    //                     errorMsg: `Failed to retrieve PageModels from `
    //                         + `\"${url}\": HTTP Status Code: `
    //                         + `${response.status}; `
    //                         + `message: ${response.statusText}`,
    //                 });
    //             }
    //         })
    //         .catch((error) => {
    //             this.setState({
    //                 ...this.state,
    //                 errorMsg: `Failed to retrieve PageModels from `
    //                     + `${url}: ${error.message}`,
    //             });
    //         });
    // }

    public render(): React.ReactNode {

        return <div className="pagemodeleditor pageModelsList">
            {this.props.pageModels.length > 0 &&
                <ul>
                    {this.props.pageModels
                        .map((pageModel: PageModel, index: number) =>
                            <PageModelListItem
                                index={index}
                                pageModel={pageModel}
                                selectPageModel={this.props.selectPageModel} />,
                    )}
                </ul>
            }
        </div>;
    }
}

interface PageModelListItemProps {
    index: number;
    pageModel: PageModel;
    selectPageModel: (selectedPageModel: PageModel) => void;
}

// interface PageModelListItemState {
//
// }

class PageModelListItem
    extends React.Component<PageModelListItemProps, {}> {

    public render(): React.ReactNode {
        return <li>
            <a data-pagemodel-id="{this.props.pageModel.pageModelId}"
                href="#"
                onClick={
                    (event) => {
                        event.preventDefault();
                        // console.log("A PageModel has been selected");
                        this.props.selectPageModel(this.props.pageModel);
                    }
                }>
                {this.props.pageModel.title}
            </a>
        </li>;
    }
}

interface PageModelComponentProps {

    ccmApplication: string;
    dispatcherPrefix: string;
    pageModel: PageModel;
    reload: () => void;
}

interface PageModelComponentState {

    containers: ContainerModel[];

    editMode: boolean;

    errorMsg: string | null;

    form: {
        name: string;
        title: string;
        description: string;
    };
}

class PageModelComponent
    extends React.Component<PageModelComponentProps, PageModelComponentState> {

    constructor(props: PageModelComponentProps) {
        super(props);

        this.state = {
            containers: this.props.pageModel.containers,
            editMode: this.props.pageModel.pageModelId === 0,
            errorMsg: null,
            form: {
                description: this.props.pageModel.description,
                name: this.props.pageModel.name,
                title: this.props.pageModel.title,
            },
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.publishPageModel = this.publishPageModel.bind(this);
    }

    public render(): React.ReactNode {

        if (this.state.editMode) {
            return <div className="bebop-body">
                {this.state.errorMsg !== null &&
                    <div className="errorPanel">
                        {this.state.errorMsg}
                    </div>
                }
                <form
                    className="pagemodeleditor pagemodel propertiesForm"
                    onSubmit={this.handleSubmit}>

                    <label htmlFor="pageModelName">
                        Name
                    </label>
                    <input
                        disabled={this.props.pageModel.pageModelId !== 0}
                        id="pageModelName"
                        onChange={this.handleChange}
                        size={32}
                        type="text"
                        value={this.state.form.name} />

                    <label htmlFor="pageModelTitle">
                        Title
                    </label>
                    <input
                        id="pageModelTitle"
                        onChange={this.handleChange}
                        size={32}
                        type="text"
                        value={this.state.form.title} />

                    <label htmlFor="pageModelDescription">
                        Description
                    </label>
                    <textarea
                        cols={80}
                        id="pageModelDescription"
                        onChange={this.handleChange}
                        rows={20}
                        value={this.state.form.description} />
                    <div>
                        <button type="submit">Save</button>
                        <button
                            id="pageModelDiscard"
                            onClick={(event) => this.discardChanges(event)}>
                            Discard changes
                        </button>
                    </div>
                </form>
            </div>;
        } else {
            return <div className="bebop-body">
                <dl className="pagemodeleditor pagemodel propertiesList">
                    <dt>Name</dt>
                    <dd>{this.props.pageModel.name}</dd>
                    <dt>Title</dt>
                    <dd>{this.props.pageModel.title}</dd>
                    <dt>Type</dt>
                    <dd>{this.props.pageModel.type}</dd>
                    <dt>Version</dt>
                    <dd>{this.props.pageModel.version.toString()}</dd>
                    <dt>Description</dt>
                    <dd>{this.props.pageModel.description}</dd>
                    <dt>Last modified</dt>
                    <dd>{this.getLastModifiedDate()}</dd>
                    <dt>Last published</dt>
                    <dd>{this.getLastPublishedDate()}</dd>
                    <dt>PublicationStatus</dt>
                    <dd>{this.props.pageModel.publicationStatus}</dd>
                </dl>
                <button onClick={(event) => {

                    event.preventDefault();

                    this.setState({
                        editMode: true,
                    });
                }}>Edit
                </button>
                {this.props.pageModel.publicationStatus
                    === PublicationStatus.NOT_PUBLISHED.toString()
                    && <button
                        onClick={this.publishPageModel}>Publish</button>
                }
                {this.props.pageModel.publicationStatus
                    === PublicationStatus.NEEDS_UPDATE.toString()
                    && <button
                        onClick={this.publishPageModel}>Republish</button>
                }
                <ContainerListComponent
                    ccmApplication={this.props.ccmApplication}
                    containers={this.props.pageModel.containers}
                    dispatcherPrefix={this.props.dispatcherPrefix}
                    pageModelName={this.props.pageModel.name} />
            </div>;
        }
    }

    private discardChanges(event: React.MouseEvent<HTMLElement>): void {

        event.preventDefault();

        this.setState({
            ...this.state,
            editMode: false,
            form: {
                description: this.props.pageModel.description,
                name: this.props.pageModel.name,
                title: this.props.pageModel.title,
            },
        });
    }

    private getLastModifiedDate(): string {

        if (this.props.pageModel.lastPublished === 0) {
            return "";
        } else {
            const lastModified: Date = new Date();
            lastModified.setTime(this.props.pageModel.lastModified);

            return lastModified.toISOString();
        }
    }

    private getLastPublishedDate(): string {

        if (this.props.pageModel.lastPublished === 0) {
            return "";
        } else {
            const lastPublished: Date = new Date();
            lastPublished.setTime(this.props.pageModel.lastPublished);

            return lastPublished.toISOString();
        }
    }

    private handleChange(event: React.ChangeEvent<HTMLElement>): void {

        const target: HTMLElement = event.target as HTMLElement;

        switch (target.id) {
            case "pageModelName": {
                const targetInput: HTMLInputElement
                    = target as HTMLInputElement;
                this.setState({
                    editMode: this.state.editMode,
                    form: {
                        description: this.state.form.description,
                        name: targetInput.value,
                        title: this.state.form.title,
                    },
                });
                break;
            }
            case "pageModelTitle": {
                const targetInput: HTMLInputElement
                    = target as HTMLInputElement;
                this.setState({
                    editMode: this.state.editMode,
                    form: {
                        description: this.state.form.description,
                        name: this.state.form.name,
                        title: targetInput.value,
                    },
                });
                break;
            }
            case "pageModelDescription": {
                const targetArea: HTMLTextAreaElement
                    = target as HTMLTextAreaElement;
                this.setState({
                    editMode: this.state.editMode,
                    form: {
                        description: targetArea.value,
                        name: this.state.form.name,
                        title: this.state.form.title,
                    },
                });
                break;
            }
        }
    }

    private handleSubmit(event: React.FormEvent<HTMLFormElement>): void {

        event.preventDefault();

        this.props.pageModel.name = this.state.form.name;
        this.props.pageModel.title = this.state.form.title;
        this.props.pageModel.description = this.state.form.description;

        const headers: Headers = new Headers();
        headers.append("Content-Type", "application/json");

        const init: RequestInit = {
            body: JSON.stringify({
                description: this.state.form.description,
                title: this.state.form.title,
            }),
            credentials: "same-origin",
            headers,
            method: "PUT",
        };

        const url: string = `${this.props.dispatcherPrefix}`
            + `/page-models/${this.props.ccmApplication}/`
            + `${this.props.pageModel.name}`;

        fetch(url, init)
            .then((response: Response) => {

                if (response.ok) {
                    this.setState({
                        ...this.state,
                        editMode: false,
                    });
                    this.props.reload();
                } else {
                    this.setState({
                        ...this.state,
                        errorMsg: `Failed to update/create PageModel: `
                            + ` ${response.status} ${response.statusText}`,
                    });
                }
            })
            .catch((error) => {
                this.setState({
                    ...this.state,
                    errorMsg: `Failed to update/create PageModel: `
                        + `${error.message}`,
                });
            });
    }

    private publishPageModel(event: React.MouseEvent<HTMLButtonElement>): void {

        event.preventDefault();

        const headers: Headers = new Headers();
        // headers.append("Content-Type", "application/json");
        headers.append("Content-Type", "application/x-www-form-urlencoded");

        // const formData: FormData = new FormData();
        // formData.set("action", "publish");
        // const data: URLSearchParams = new URLSearchParams();
        // data.append("action", "publish");

        const init: RequestInit = {

            body: "action=publish",
            credentials: "same-origin",
            headers,
            method: "POST",
        };

        const url: string = `${this.props.dispatcherPrefix}`
            + `/page-models/${this.props.ccmApplication}/`
            + `${this.props.pageModel.name}`;

        fetch(url, init)
            .then((response: Response) => {
                if (response.ok) {

                    this.props.reload();
                } else {
                    this.setState({
                        ...this.state,
                        errorMsg: `Failed to publish PageModel: `
                            + ` ${response.status} ${response.statusText}`,
                    });
                }
            })
            .catch((error) => {
                this.setState({
                    ...this.state,
                    errorMsg: `Failed to publish PageModel: ${error.message}`,
                });
            });

    }

}

interface ContainerListProps {

    ccmApplication: string;
    containers: ContainerModel[];
    dispatcherPrefix: string;
    pageModelName: string;
}

interface ContainerListState {

    containerName: string;
    containers: ContainerModel[];
    errorMsg: string;
}

class ContainerListComponent
    extends React.Component<ContainerListProps, ContainerListState> {

    constructor(props: ContainerListProps) {

        super(props);

        this.state = {
            containerName: "",
            containers: props.containers,
            errorMsg: "",
        };

        this.addContainer = this.addContainer.bind(this);
        this.deleteContainer = this.deleteContainer.bind(this);
        this.updateNewContainerName = this.updateNewContainerName.bind(this);
    }

    public render(): React.ReactNode {
        return <div className="containerList">
            <form onSubmit={this.addContainer}>
                <label htmlFor="newContainerName">
                    Name of new container
                </label>
                <input id="newContainerName"
                    onChange={this.updateNewContainerName}
                    size={32}
                    type="text"
                    value={this.state.containerName} />
                <button type="submit">
                    <span className="fa fa-plus-circle"></span>
                    Add container
                </button>
            </form>
            {this.state.errorMsg !== ""
                && <div className="errorPanel">
                    <span className="fa fa-exclamation-triangle"></span>
                    {this.state.errorMsg}
                </div>}
            <ul className="containerList">
                {this.state.containers
                    && this.props.containers.map((container) =>
                        <ContainerModelComponent
                            ccmApplication={this.props.ccmApplication}
                            container={container}
                            deleteContainer={this.deleteContainer}
                            dispatcherPrefix={this.props.dispatcherPrefix}
                            errorMsg=""
                            pageModelName={this.props.pageModelName} />)
                        // <li>
                        //     <span>{container.key}</span>
                        //     <button
                        //         // onClick={this.deleteContainer}
                        //         data-containerKey={container.key}>
                        //         <span className="fa fa-minus-circle"></span>
                        //         Delete
                        //     </button>
                        // </li>)
                }
            </ul>
        </div>;
    }

    private updateNewContainerName(
        event: React.ChangeEvent<HTMLElement>): void {

        const target: HTMLInputElement = event.target as HTMLInputElement;

        this.setState({
            ...this.state,
            containerName: target.value,
        });
    }

    private addContainer(
        event: React.FormEvent<HTMLFormElement>): void {

        event.preventDefault();

        if (this.state.containerName === null
            || this.state.containerName === "") {

            this.setState({
                ...this.state,
                errorMsg: "A container needs a name!",
            });

            return;
        }

        if (this.state.containers.findIndex((container: ContainerModel) => {
            return container.key === this.state.containerName;
        }) >= 0) {

            this.setState({
                ...this.state,
                errorMsg: `A container with the key `
                    + `"${this.state.containerName}" already exists.`,
            });

            return;
        }

        const headers: Headers = new Headers();
        headers.append("Content-Type", "application/json");

        const init: RequestInit = {

            body: JSON.stringify({}),
            credentials: "same-origin",
            headers,
            method: "PUT",
        };

        const url: string = `${this.props.dispatcherPrefix}`
            + `/page-models/${this.props.ccmApplication}/`
            + `${this.props.pageModelName}`
            + `/containers/`
            + `${this.state.containerName}`;

        fetch(url, init)
            .then((response: Response) => {
                if (response.ok) {

                    response
                        .json()
                        .then((newContainer) => {

                            const containers = [
                                ...this.state.containers,
                                newContainer,
                            ];
                            containers.sort((container1, container2) => {
                                const key1: string = container1.key;
                                const key2: string = container2.key;

                                if (key1 < key2) {
                                    return -1;
                                } else if (key1 > key2) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            });

                            this.setState({
                                ...this.state,
                                containerName: "",
                                containers,
                            });
                        })
                        .catch((error) => {
                            this.setState({
                                ...this.state,
                                errorMsg: `Failed to parse response: `
                                    + `${error.message}`,
                            });
                        });
                } else {
                    this.setState({
                        ...this.state,
                        errorMsg: `Failed to create new container: `
                            + `${response.status} ${response.statusText}`,
                    });
                }
            })
            .catch((error) => {
                this.setState({
                    ...this.state,
                    errorMsg: `Failed to create new container: `
                        + `${error.message}`,
                });
            });
    }

    private deleteContainer(containerKey: string): void {

        const init: RequestInit = {

            body: JSON.stringify({}),
            credentials: "same-origin",
            method: "DELETE",
        };

        const url: string = `${this.props.dispatcherPrefix}`
            + `/page-models/${this.props.ccmApplication}/`
            + `${this.props.pageModelName}`
            + `/containers/${containerKey}`;

        fetch(url, init)
            .then((response: Response) => {

                if (response.ok) {

                    const containers = this
                        .state
                        .containers
                        .filter((container) => {
                            return container.key !== containerKey;
                        });

                    this.setState({
                        ...this.state,
                        containers,
                    });

                } else {
                    this.setState({
                        ...this.state,
                        errorMsg: `Failed to delete container `
                            + `"${containerKey}": `
                            + ` ${response.status} ${response.statusText}`,
                    });
                }

            })
            .catch((error) => {
                this.setState({
                    ...this.state,
                    errorMsg: `Failed to delete container `
                        + `"${containerKey}": ${error.message}`,
                });
            });
    }
}

interface ContainerModelComponentProps {

    ccmApplication: string;
    container: ContainerModel;
    deleteContainer: (key: string) => void;
    dispatcherPrefix: string;
    errorMsg: string;
    pageModelName: string;
}

interface ContainerModelComponentState {

    components: ComponentModel[];
    errorMessages: string[];
}

class ContainerModelComponent
    extends React.Component<ContainerModelComponentProps,
                            ContainerModelComponentState> {

    constructor(props: ContainerModelComponentProps) {

        super(props);

        this.state = {

            components: [],
            errorMessages: [],
        };
    }

    public componentDidMount() {

        this.fetchComponents();
    }

    public render(): React.ReactNode {

        return <li>
            <div className="container-header">
                <span>{this.props.container.key}</span>
                <button
                    onClick={(event) => this.deleteContainer(
                        event,
                        this.props.container.key)}>
                    <span className="fa fa-minus-circle"></span>
                    Delete
                </button>
            </div>
            <div className="components-list">
                <ul>
                    {this.state.components.map((component: ComponentModel) =>
                        this.getComponentModelEditor(component),
                    )}
                </ul>
            </div>
        </li>;
    }

    private fetchComponents(): void {

        const componentsUrl = `${this.props.dispatcherPrefix}`
            + `/page-models/${this.props.ccmApplication}`
            + `/${this.props.pageModelName}`
            + `/containers/${this.props.container.key}`
            + `/components`;

        const init: RequestInit = {
            credentials: "same-origin",
            method: "GET",
        };

        fetch(componentsUrl, init)
            .then((response) => {
                if (response.ok) {

                    response
                        .json()
                        .then((components) => {

                            this.setState({
                                ...this.state,
                                components,
                            });
                        })
                        .catch((error) => {
                            const errorMessages: string[] = this
                                .state.errorMessages;
                            errorMessages.push(`Failed to retrieve PageModels `
                                + `from ${componentsUrl}: ${error.message}`);

                            this.setState({
                                ...this.state,
                                errorMessages,
                            });
                        });
                } else {
                    const errorMessages: string[] = this
                        .state.errorMessages;
                    errorMessages.push(`Failed to retrieve PageModels from `
                        + `\"${componentsUrl}\": HTTP Status Code: `
                        + `${response.status}; `
                        + `message: ${response.statusText}`);

                    this.setState({
                        ...this.state,
                        errorMessages,
                    });
                }
            })
            .catch((error) => {
                const errorMessages: string[] = this
                    .state.errorMessages;
                errorMessages.push(`Failed to retrieve PageModels ` +
                    `from ${componentsUrl}: ${error.message}`);

                this.setState({
                    ...this.state,
                    errorMessages,
                });
            });
    }

    private getComponentModelEditor(
        component: ComponentModel): React.ReactNode {

        if (PageModelEditor.getAvailableComponents()[component.type]) {
            console.log(`Found editor generator: ${PageModelEditor.getAvailableComponents()[component.type]}`);
            return PageModelEditor
                .getAvailableComponents()[component.type](component);
        } else {
            return <DefaultComponentModelEditor component={component} />;
        }
    }

    private deleteContainer(
        event: React.MouseEvent<HTMLButtonElement>,
        containerKey: string): void {

        event.preventDefault();

        this.props.deleteContainer(containerKey);
    }
}

interface ComponentModelEditorProps<C extends ComponentModel> {

    component: C;
}

interface ComponentModelEditorState {

    dialogExpanded: string;
}

abstract class AbstractComponentModelEditor<
    C extends ComponentModel,
    P extends ComponentModelEditorProps<C>,
    S extends ComponentModelEditorState>

    extends React.Component<P, S> {

    constructor(props: ComponentModelEditorProps<C>) {

        super(props as any);

        this.setState({
            ...this.state as any,
            dialogExpanded: "dialogClosed",
        });
    }

    public abstract renderPropertyList(): React.ReactFragment;

    public abstract renderEditorDialog(): React.ReactFragment;

    public render(): React.ReactNode {

        return <li className="componentModelEditor">
            <dl>
                <dt>Key</dt>
                <dd>{this.props.component.key}</dd>
                <dt>Type</dt>
                <dd>{this.props.component.type}</dd>
                {this.renderPropertyList}
            </dl>
            <button onClick={this.toggleEditorDialog}>
                Edit
            </button>
            <dialog className="{this.state.dialogExpanded}">
                {this.renderEditorDialog}
            </dialog>
        </li>;
    }

    private toggleEditorDialog(
        event: React.MouseEvent<HTMLButtonElement>): void {

        if (this.state.dialogExpanded === "dialogExpanded") {
            this.setState({
                ...this.state as any,
                dialogExpanded: "dialogClosed",
            });
        } else {
            this.setState({
                ...this.state as any,
                dialogExpanded: "dialogExpanded",
            });
        }
    }
}

class DefaultComponentModelEditor
    extends AbstractComponentModelEditor<
        ComponentModel,
        ComponentModelEditorProps<ComponentModel>,
        ComponentModelEditorState> {

    constructor(props: ComponentModelEditorProps<ComponentModel>) {

        super(props);

        this.setState({
            dialogExpanded: "dialogClosed",
        });
    }

    public renderPropertyList(): React.ReactFragment {

        return <React.Fragment />;
    }

    public renderEditorDialog(): React.ReactFragment {

        return <React.Fragment />;
    }

    public render(): React.ReactNode {
        return super.render();
    }

}

// interface PageModelEditorProps {
//
// }
//
interface PageModelEditorState {

    selectedPageModel: PageModel | null;

}

interface PageModelEditorState {

    errorMessages: string[];
    pageModels: PageModel[];
    selectedPageModel: PageModel | null;
}

interface ComponentInfo {

    javaClass: string;
    editorComponent: any;
    label: string;
}

class PageModelEditor
    extends React.Component<{}, PageModelEditorState> {

    public static getAvailableComponents(): {
        [type: string]: (component: ComponentModel) => React.ReactFragment} {

        console.log("Available editors:");
        console.log(PageModelEditor.componentModelEditors.toString());

        return {
            ...PageModelEditor.componentModelEditors,
        };
    }

    public static registerComponentModelEditor(
        type: string,
        generator: ((component: ComponentModel) => React.ReactFragment)): void {

        PageModelEditor.componentModelEditors = {
            ...PageModelEditor.componentModelEditors,
            type: generator,
        };
    }

    private static componentModelEditors: {
        [type: string]: (component: ComponentModel) => React.ReactFragment;
    };

    private static getDispatcherPrefix(): string {

        const dataElem: HTMLElement | null = document
            .querySelector("#page-models-editor.react-data");

        if (dataElem === null) {
            return "";
        } else {
            const value: string | null
                = dataElem.getAttribute("data-dispatcher-prefix");
            if (value === null) {
                return "";
            } else {
                return value;
            }
        }
    }

    private static getCcmApplication(): string {

        const dataElem: HTMLElement | null = document
            .querySelector("#page-models-editor.react-data");

        if (dataElem === null) {
            return "???";
        } else {
            const value: string | null
                = dataElem.getAttribute("data-ccm-application");
            if (value === null) {
                return "???";
            } else {
                return value;
            }
        }
    }

    constructor(props: any) {

        super(props);

        // this.state = {
        //     // selectedPageModel: newPageModel,
        //     context: {
        //         pageModelSelected: false,
        //         selectedPageModel: newPageModel,
        //     },
        // };
        this.state = {
            errorMessages: [],
            pageModels: [],
            selectedPageModel: null,
        };

        this.createNewPageModel = this.createNewPageModel.bind(this);
        this.selectPageModel = this.selectPageModel.bind(this);
    }

    public componentDidMount() {

        this.fetchPageModels();
    }

    public render(): React.ReactNode {

        return <React.Fragment>
            <div id="left">
                <div className="column-head"></div>
                <div className="column-content">
                    <div className="bebop-left">
                        <div className="bebop-segmented-panel">
                            <div className="bebop-segment">
                                <h3 className="bebop-segment-header">
                                    Available PageModels
                                </h3>
                                <div className="bebop-segment-body">
                                    <button
                                        className="pagemodeleditor addbutton"
                                        onClick={this.createNewPageModel}>
                                        <span className="fa fa-plus-circle">
                                        </span>
                                        Create new PageModel
                                    </button>
                                    <PageModelsList
                                        ccmApplication={PageModelEditor
                                            .getCcmApplication()}
                                        dispatcherPrefix={PageModelEditor
                                            .getDispatcherPrefix()}
                                        pageModels={this.state.pageModels}
                                        selectPageModel
                                        ={this.selectPageModel} />
                                    <button
                                        className="pagemodeleditor addbutton"
                                        onClick={this.createNewPageModel}>
                                        <span className="fa fa-plus-circle">
                                        </span>
                                        Create new PageModel
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div id="right">
                <div className="column-head">
                </div>
                <div className="column-content">
                    {this.state.errorMessages.length > 0 &&
                        <div className="errorPanel">
                            {this.state.errorMessages.map((msg) => {
                                return <p>
                                    {msg}
                                </p>;
                            })}
                        </div>
                    }
                    {this.state.selectedPageModel !== null &&
                        <PageModelComponent
                            ccmApplication={PageModelEditor.getCcmApplication()}
                            dispatcherPrefix
                            ={PageModelEditor.getDispatcherPrefix()}
                            pageModel={this.state.selectedPageModel}
                            reload={() => {
                                this.reload();
                            }} />
                    }
                </div>
            </div>
        </React.Fragment>;
    }

    private createNewPageModel(
        event: React.MouseEvent<HTMLButtonElement>): void {

        event.preventDefault();

        this.setState({
            ...this.state,
            selectedPageModel: {
                containers: [],
                description: "",
                lastModified: 0,
                lastPublished: 0,
                modelUuid: "",
                name: "",
                pageModelId: 0,
                publicationStatus: PublicationStatus.NOT_PUBLISHED.toString(),
                title: "",
                type: "",
                uuid: "",
                version: PageModelVersion.DRAFT,
            },
        });
    }

    private fetchPageModels(): void {

        const init: RequestInit = {
            credentials: "same-origin",
            method: "GET",
        };

        const url: string = `${PageModelEditor.getDispatcherPrefix()}`
            + `/page-models/${PageModelEditor.getCcmApplication()}`;

        fetch(url, init)
            .then((response) => {
                if (response.ok) {
                    response
                        .json()
                        .then((pageModels: PageModel[]) => {
                            this.setState({
                                ...this.state,
                                pageModels,
                            });
                        })
                        .catch((error) => {
                            const errorMessages: string[] = this
                                .state.errorMessages;
                            errorMessages.push(`Failed to retrieve PageModels `
                                + `from ${url}: ${error.message}`);

                            this.setState({
                                ...this.state,
                                errorMessages,
                            });
                        });
                } else {
                    const errorMessages: string[] = this
                        .state.errorMessages;
                    errorMessages.push(`Failed to retrieve PageModels from `
                        + `\"${url}\": HTTP Status Code: `
                        + `${response.status}; `
                        + `message: ${response.statusText}`);

                    this.setState({
                        ...this.state,
                        errorMessages,
                    });
                }
            })
            .catch((error) => {
                const errorMessages: string[] = this
                    .state.errorMessages;
                errorMessages.push(`Failed to retrieve PageModels ` +
                    `from ${url}: ${error.message}`);

                this.setState({
                    ...this.state,
                    errorMessages,
                });
            });
    }

    private reload(): void {

        this.fetchPageModels();
        // this.setState({
        //     ...this.state,
        //     selectedPageModel: null,
        // });
    }

    private selectPageModel(selectedPageModel: PageModel): void {

        this.setState(
            (state: PageModelEditorState) => {
                return {
                    ...state,
                    selectedPageModel,
                };
            });
    }

    // private setSelectedPageModel(selectedPageModel: PageModel): void {
    //
    //     this.setState((state: any) => {
    //         return {
    //             ...state,
    //             selectedPageModel,
    //         };
    //     });
    // }
}
