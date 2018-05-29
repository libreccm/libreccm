import * as React from "react";

export { PageModelEditor };

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

class PageModelEditor extends React.Component<{}, {}> {

    private getCcmApplication(): string {

        const dataElem: HTMLElement | null = document
            .querySelector("#page-models-editor.react-data");

        if (dataElem === null) {
            return "???";
        } else {
            const value: string | null = dataElem.getAttribute("data-ccm-application");
            if (value === null) {
                return "???";
            } else {
                return value;
            }
        }
    }

    public render() {



        return <React.Fragment>
            <div id="left">
                <div className="column-head"></div>
                <div className="column-content">
                    List of available page models placeholder
                </div>
            </div>
            <div id="right">
                <div className="column-head">
                </div>
                <div className="column-content">
                    PageModelEditor Placeholder
                    <pre>
                        {this.getCcmApplication()}
                    </pre>
                </div>
            </div>
        </React.Fragment>;
    }
}
