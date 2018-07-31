import * as React from "react";
import { render } from "react-dom";

import {
        // AbstractComponentModelEditor,
        ComponentModel,
        BasicComponentModelEditorDialog,
        BasicComponentModelEditorDialogProps,
        BasicComponentModelEditorDialogState,
        BasicComponentModelPropertiesList,
        BasicComponentModelPropertiesListProps,
        ComponentModelEditor,
        ComponentModelEditorProps,
        ComponentModelEditorDialogProps,
        ComponentInfo,
        EditorComponents,
         // ComponentModelEditorProps,
         // ComponentModelEditorState,
         PageModelEditor,
} from "ccm-pagemodelseditor";

class CategoryTreeComponentPropertiesList
    extends React.Component<
        BasicComponentModelPropertiesListProps<CategoryTreeComponent>, {}> {

    constructor(
        props: BasicComponentModelPropertiesListProps<CategoryTreeComponent>) {

        super(props);
    }

    public render(): React.ReactNode {

        return <BasicComponentModelPropertiesList
            component={this.props.component}>
            <dt>showFullTree</dt>
            <dd>{this.props.component.showFullTree}</dd>
        </BasicComponentModelPropertiesList>
    }
}

interface CategoryTreeComponentEditorDialogState
    extends BasicComponentModelEditorDialogState {

    showFullTree: boolean;
}

class CategoryTreeComponentEditorDialog
    extends React.Component<
        BasicComponentModelEditorDialogProps<CategoryTreeComponent>,
        CategoryTreeComponentEditorDialogState> {

    constructor(
        props: BasicComponentModelEditorDialogProps<CategoryTreeComponent>) {

        super(props);

        this.state = {
            ...this.state,
            showFullTree: this.props.component.showFullTree,
        };

        this.handleChange = this.handleChange.bind(this);
    }

    public render(): React.ReactNode {

        const idPrefix: string
            = `${this.props.containerKey}_${this.props.component.key}_`;

        return <BasicComponentModelEditorDialog
            ccmApplication={this.props.ccmApplication}
            component={this.props.component}
            containerKey={this.props.containerKey}
            dispatcherPrefix={this.props.dispatcherPrefix}
            getComponentModelProperties={this.getComponentModelProperties}
            pageModelName={this.props.pageModelName}>

            <label htmlFor={`${idPrefix}showFullTree`}>Show full tree?</label>
            <input checked={this.props.component.showFullTree}
                   id={`${idPrefix}showFullTree`}
                   onChange={this.handleChange}
                   type="checkbox" />
        </BasicComponentModelEditorDialog>
    }

    private getComponentModelProperties(): {[name: string]: any} {

        return {
            showFullTree: this.state.showFullTree,
        };
    }

    private handleChange(event: React.ChangeEvent<HTMLInputElement>): void {

        const target: HTMLInputElement = event.currentTarget;
        const idPrefix: string
            = `${this.props.containerKey}_${this.props.component.key}_`;

        switch(target.id) {
            case `${idPrefix}showFullTree`: {
                this.setState({
                    ...this.state,
                    showFullTree: target.checked,
                });
                break;
            }
        }
    }
}

class ItemListComponentPropertiesList extends React.Component<
    BasicComponentModelPropertiesListProps<ItemListComponent>, {}> {

    constructor(
        props: BasicComponentModelPropertiesListProps<ItemListComponent>) {

        super(props);
    }

    public render(): React.ReactNode {

        return <BasicComponentModelPropertiesList
            component={this.props.component}>
            <dt>Descending?</dt>
            <dd>{this.props.component.descending}</dd>
            <dt>Limit to type</dt>
            <dd>{this.props.component.limitToType}</dd>
            <dt>Page size</dt>
            <dd>{this.props.component.pageSize}</dd>
            <dt>List Order</dt>
            <dd>
                {Array.isArray(this.props.component.listOrder) ?
                    (
                        this.props.component.listOrder.map((order) => {
                            <li>
                                {order}
                            </li>
                        })
                    ) : ("No order set")
                }
            </dd>
        </BasicComponentModelPropertiesList>
    }
}

interface ItemListComponentEditorDialogState
    extends BasicComponentModelEditorDialogState {

    descending: boolean;
    limitToType: string;
    pageSize: number;
    listOrder: string[];
}

class ItemListComponentEditorDialog extends React.Component<
    BasicComponentModelEditorDialogProps<ItemListComponent>,
    ItemListComponentEditorDialogState> {

    constructor(
        props: BasicComponentModelEditorDialogProps<ItemListComponent>) {

        super(props);

        this.state = {
            ...this.state,
            descending: this.props.component.descending,
            limitToType: this.props.component.limitToType,
            pageSize: this.props.component.pageSize,
            listOrder: this.props.component.listOrder,
        };

        this.handleChange = this.handleChange.bind(this);
    }

    public render(): React.ReactNode {

        const idPrefix: string
            = `${this.props.containerKey}_${this.props.component.key}_`;

        return <BasicComponentModelEditorDialog
            ccmApplication={this.props.ccmApplication}
            component={this.props.component}
            containerKey={this.props.containerKey}
            dispatcherPrefix={this.props.dispatcherPrefix}
            pageModelName={this.props.pageModelName}
            getComponentModelProperties={this.getComponentModelProperties}>
            <label htmlFor={`${idPrefix}descending`}>
                Descending?
            </label>
            <input checked={this.state.descending}
                   id={`${idPrefix}descending`}
                   onChange={this.handleChange}
                   type="checkbox" />
            <label htmlFor={`${idPrefix}limitToType`}>
                Limit to type
            </label>
            <input id={`${idPrefix}limitToType`}
                   maxLength={1024}
                   onChange={this.handleChange}
                   size={64}
                   type="text"
                   value={this.state.limitToType}/>
            <label htmlFor={`{$idPrefix}pageSize`}>
                Page size
            </label>
            <input id={`${idPrefix}pageSize`}
                   min="1"
                   onChange={this.handleChange}
                   type="number"
                   value={this.state.pageSize}/>
            <label htmlFor={`${idPrefix}listOrder`}>
                List Order
            </label>
            <textarea cols={40}
                      id={`${idPrefix}listOrder`}
                      onChange={this.handleListOrderChange}
                      rows={5}>
                {Array.isArray(this.state.listOrder) ? (
                    this.state.listOrder.join("\n")
                ) : (
                    ""
                )}
            </textarea>

        </BasicComponentModelEditorDialog>;
    }

    private getComponentModelProperties(): {[name: string]: any} {

        return {
            descending: this.state.descending,
            limitToType: this.state.limitToType,
            pageSize: this.state.pageSize,
            listOrder: this.state.listOrder,
        };
    }

    private handleChange(event: React.ChangeEvent<HTMLInputElement>): void {

        const target: HTMLInputElement = event.currentTarget;
        const idPrefix: string
            = `${this.props.containerKey}_${this.props.component.key}_`;

        switch(target.id) {
            case `${idPrefix}descending`: {
                this.setState({
                    ...this.state,
                    descending: target.checked,
                });
                break;
            }
            case `${idPrefix}limitToType`: {
                this.setState({
                    ...this.state,
                    limitToType: target.value,
                });
                break;
            }
            case `${idPrefix}pageSize`: {
                this.setState({
                    ...this.state,
                    pageSize: target.valueAsNumber,
                });
                break;
            }
        }
    }

    private handleListOrderChange(
        event: React.ChangeEvent<HTMLTextAreaElement>) {

        const target: HTMLTextAreaElement = event.currentTarget;
        const componentId: string
            = `${this.props.containerKey}_${this.props.component.key}`
                + `_listOrder`;

        this.setState({
            ...this.state,
            listOrder: target.value.split("\n"),
        });

    }

}

// PageModelEditor.registerComponentModelEditor(
//     "org.librecms.pagemodel.CategoryTreeComponent",
//     (props: ComponentModelEditorProps<ComponentModel>) => {
//         return <CategoryTreeComponentEditor
//             ccmApplication={props.ccmApplication}
//             component={props.component as CategoryTreeComponent}
//             containerKey={props.containerKey}
//             dispatcherPrefix={props.dispatcherPrefix}
//             pageModelName={props.pageModelName} />
//     }
// );
// PageModelEditor.registerComponentModelEditor(
//     "org.librecms.pagemodel.ItemListComponent",
//     (props: ComponentModelEditorProps<ComponentModel>) => {
//         return <ItemListComponentEditor
//             ccmApplication={props.ccmApplication}
//             component={props.component as ItemListComponent}
//             containerKey={props.containerKey}
//             dispatcherPrefix={props.dispatcherPrefix}
//             pageModelName={props.pageModelName} />
//     }
// );

render(
    <PageModelEditor />,
    document.getElementById("cms-content"),
);

interface CategoryTreeComponent extends ComponentModel {

    showFullTree: boolean;
}

// interface CategoryTreeComponentEditorProps
//     extends ComponentModelEditorProps<CategoryTreeComponent> {
//
// }
//
// interface CategoryTreeComponentEditorState
//     extends ComponentModelEditorState {
//
//     showFullTree: boolean;
// }
//
//
// class CategoryTreeComponentEditor
//     extends AbstractComponentModelEditor<CategoryTreeComponent,
//                                          CategoryTreeComponentEditorProps,
//                                          CategoryTreeComponentEditorState> {
//
//     public constructor(props: CategoryTreeComponentEditorProps) {
//
//         super(props);
//
//         this.setState({
//             ...this.state as any,
//             dialogExpanded: "dialogClosed",
//             showFullTree: false,
//         });
//     }
//
//     public renderPropertyList(): React.ReactFragment {
//
//         return <React.Fragment>
//             <dt>Show full tree</dt>
//             <dd>{this.props.component.showFullTree}</dd>
//         </React.Fragment>;
//     }
//
//     public renderEditorDialog(): React.ReactFragment {
//
//         return <React.Fragment />;
//     }
// }

interface ItemListComponent extends ComponentModel {

    descending: boolean;
    limitToType: string;
    pageSize: number;
    listOrder: string[],
}

ComponentModelEditor.registerEditorComponents(
    "org.librecms.pagemodel.CategoryTreeComponent",
    {
        editorDialog:
            CategoryTreeComponentEditorDialog as typeof React.Component,
        propertiesList:
            CategoryTreeComponentPropertiesList as typeof React.Component,
    }
);

ComponentModelEditor.registerEditorComponents(
    "org.librecms.pagemodel.ItemListComponent",
    {
        editorDialog:
            ItemListComponentEditorDialog as typeof React.Component,
        propertiesList:
            ItemListComponentPropertiesList as typeof React.Component,
    }
);


// interface ItemListComponentEditorProps
//     extends ComponentModelEditorProps<ItemListComponent> {
//
// }
//
// interface ItemListComponentEditorState
//     extends ComponentModelEditorState {
//
//     descending: boolean;
//     limitToType: string;
//     pageSize: number;
//     listOrder: string[];
// }
//
// class ItemListComponentEditor
//     extends AbstractComponentModelEditor<ItemListComponent,
//                                          ItemListComponentEditorProps,
//                                          ItemListComponentEditorState> {
//
//     public constructor(props: ItemListComponentEditorProps) {
//
//         super(props as any);
//
//         this.setState({
//             ...this.state,
//             dialogExpanded: "dialogClosed",
//             descending: false,
//             limitToType: "",
//             pageSize: 30,
//             listOrder: [],
//         });
//     }
//
//     public renderPropertyList(): React.ReactFragment {
//
//         console.log("Rendering properties list for ItemListComponent...");
//         console.log(`listOrder = ${this.props.component.listOrder}`);
//
//         return <React.Fragment>
//             <dt>descending</dt>
//             <dd>{this.props.component.descending}</dd>
//             <dt>limitToType</dt>
//             <dd>{this.props.component.limitToType}</dd>
//             <dt>pageSize</dt>
//             <dd>{this.props.component.pageSize}</dd>
//             <dt>listOrder</dt>
//             <dd>
//                 {Array.isArray(this.props.component.listOrder) ?
//                     (
//                         this.props.component.listOrder.map((order) => {
//                             <li>
//                                 {order}
//                             </li>
//                         })
//                     ) : ("No order set")
//                 }
//             </dd>
//         </React.Fragment>
//     }
//
//     public renderEditorDialog(): React.ReactFragment {
//
//         return <React.Fragment>
//             <label htmlFor="descending">
//                 Descending?
//             </label>
//             <input checked={this.props.component.descending}
//                    id="descending"
//                    type="checkbox" />
//             <label htmlFor="limitToType">Limit to type</label>
//             <input id="limitToType"
//                    maxLength={1024}
//                    size={64}
//                    type="text"
//                    value={this.props.component.limitToType}/>
//             <label htmlFor="pageSize">Page size</label>
//             <input id="pageSize"
//                    min="1"
//                    type="number"
//                    value={this.props.component.pageSize}/>
//             <label htmlFor="listOrder">List Order</label>
//             <textarea cols={40} id="listOrder" rows={5}>
//                 {Array.isArray(this.props.component.listOrder) ? (
//                     this.props.component.listOrder.join(", ")
//                 ) : (
//                     ""
//                 )}
//             </textarea>
//         </React.Fragment>;
//     }
// }
