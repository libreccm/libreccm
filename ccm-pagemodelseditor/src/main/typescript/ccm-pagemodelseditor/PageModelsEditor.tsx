import * as React from "react";
import { PageModel, PageModelVersion } from "./datatypes";

export {
    PageModelEditor,
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

interface PageModelEditorContext {

    pageModelSelected: boolean;
    selectedPageModel: PageModel;
}

const newPageModel: PageModel = {
    description: "New PageModel",
    modelUuid: "",
    name: "newPageModel",
    pageModelId: 0,
    title: "A new PageModel",
    type: "",
    uuid: "",
    version: PageModelVersion.DRAFT,
};

const pageModelEditorContext = React.createContext({
    pageModelSelected: false,
    selectedPageModel: newPageModel,
});

interface PageModelsListProps {

    ccmApplication: string;
    dispatcherPrefix: string;
    selectPageModel: (selectedPageModel: PageModel) => void;
}

interface PageModelsListState {

    errorMsg: string | null;
    pageModels: PageModel[];
}

class PageModelsList
    extends React.Component<PageModelsListProps, PageModelsListState> {

    constructor(props: PageModelsListProps) {
        super(props);

        this.state = {
            errorMsg: null,
            pageModels: [],
        };
    }

    public componentDidMount() {

        const init: RequestInit = {
            credentials: "same-origin",
            method: "GET",
        };

        const url: string = `${this.props.dispatcherPrefix}`
            + `/page-models/${this.props.ccmApplication}`;

        fetch(url, init)
            .then((response: Response) => {
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
                            this.setState({
                                ...this.state,
                                errorMsg: `Failed to retrieve PageModels from `
                                    + `${url}: ${error.message}`,
                            });
                        });
                } else {
                    this.setState({
                        ...this.state,
                        errorMsg: `Failed to retrieve PageModels from `
                            + `\"${url}\": HTTP Status Code: `
                            + `${response.status}; `
                            + `message: ${response.statusText}`,
                    });
                }
            })
            .catch((error) => {
                this.setState({
                    ...this.state,
                    errorMsg: `Failed to retrieve PageModels from `
                        + `${url}: ${error.message}`,
                });
            });
    }

    public render() {

        return <div className="pageModelsList">
            {this.state.errorMsg !== null &&
                <div className="errorPanel">
                    {this.state.errorMsg}
                </div>
            }
            {this.state.pageModels.length > 0 &&
                <ul>
                    {this.state.pageModels.map((pageModel: PageModel) =>
                        <PageModelListItem pageModel={pageModel}
                            selectPageModel={this.props.selectPageModel} />,
                    )}
                </ul>
            }
        </div>;
    }
}

interface PageModelListItemProps {
    pageModel: PageModel;
    selectPageModel: (selectedPageModel: PageModel) => void;
}

// interface PageModelListItemState {
//
// }

class PageModelListItem
    extends React.Component<PageModelListItemProps, {}> {

    private onClick(event: any): void {

        console.log(`Setting selected PageModel. this is ${this}`);
        this.props.selectPageModel(this.props.pageModel);
    }

    public render() {
        return <li>
            <a data-pagemodel-id="{this.props.pageModel.pageModelId}"
                href="#"
                onClick={
                    (event) => {
                        console.log("A PageModel has been selected");
                        this.props.selectPageModel(this.props.pageModel)
                    }
                }>
                {this.props.pageModel.title}
            </a>
        </li>;
    }
}

// interface PageModelEditorProps {
//
// }
//
// interface PageModelEditorState {
//
//     selectedPageModel: PageModel | null;
//
// }

class PageModelEditor
    extends React.Component<{}, any> {

    constructor(props: any) {

        super(props);

        this.state = {
            // selectedPageModel: newPageModel,
            context: {
                pageModelSelected: false,
                selectedPageModel: newPageModel,
            },
        };
    }

    public render() {

        return <React.Fragment>
            <pageModelEditorContext.Provider value={this.state.context}>
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
                                            className="pagemodels addbutton">
                                            <span>+</span> Create new PageModel
                                        </button>
                                        <PageModelsList
                                            ccmApplication={this.getCcmApplication()}
                                            dispatcherPrefix={this.getDispatcherPrefix()}
                                            selectPageModel={(pageModel: PageModel) => {
                                                this.setState((state: any) => {
                                                    console.log("Updating state for selectedPageModel");
                                                    return {
                                                        // ...state,
                                                        context: {
                                                            pageModelSelected: true,
                                                            selectedPageModel: pageModel,
                                                        }
                                                    };
                                                });
                                            }} />
                                        <button className="pagemodels addbutton">
                                            <span>+</span> Create new PageModel
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
                        PageModelEditor Placeholder
                    <pageModelEditorContext.Consumer>
                            {(context) =>
                                <React.Fragment>
                                    <pre>
                                        pageModelSelected: {context.pageModelSelected ? "true" : "false" }
                                    </pre>
                                    {context.pageModelSelected && <pre>

                                        {context.selectedPageModel.name}
                                    </pre>
                                    }
                                </React.Fragment>
                            }
                        </pageModelEditorContext.Consumer>
                        <pre>
                            {this.getCcmApplication()}
                        </pre>
                    </div>
                </div>
            </pageModelEditorContext.Provider>
        </React.Fragment>;
    }

    private getDispatcherPrefix(): string {

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

    private getCcmApplication(): string {

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
