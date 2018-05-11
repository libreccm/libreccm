import * as React from "react";

export { PageModelEditor };

class PageModelEditor extends React.Component<{}, {}> {

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
                        {document.querySelector("#page-models-editor.react-data").getAttribute("data-ccm-application")}
                    </pre>
                </div>
            </div>
        </React.Fragment>;
    }
}
