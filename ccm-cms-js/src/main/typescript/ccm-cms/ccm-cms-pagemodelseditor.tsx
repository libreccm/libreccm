import * as React from "react";
import { render } from "react-dom";

import {
        BasicComponentModelEditorDialog,
        BasicComponentModelEditorDialogProps,
        BasicComponentModelEditorDialogState,
        BasicComponentModelPropertiesList,
        BasicComponentModelPropertiesListProps,
        ComponentInfo,
        ComponentModel,
        ComponentModelEditor,
        ComponentModelEditorDialogProps,
        ComponentModelEditorProps,
        EditorComponents,
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
        </BasicComponentModelPropertiesList>;
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
        </BasicComponentModelEditorDialog>;
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

        switch (target.id) {
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
                        this.props.component.listOrder.map((order) =>
                            <li>
                                {order}
                            </li>,
                        )
                    ) : ("No order set")
                }
            </dd>
        </BasicComponentModelPropertiesList>;
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
            listOrder: this.props.component.listOrder,
            pageSize: this.props.component.pageSize,
        };

        this.handleChange = this.handleChange.bind(this);

        this.getComponentModelProperties
            = this.getComponentModelProperties.bind(this);
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
                      rows={5}
                      value={Array.isArray(this.state.listOrder) ? (
                          this.state.listOrder.join("\n")
                      ) : (
                          ""
                      )}>
            </textarea>

        </BasicComponentModelEditorDialog>;
    }

    private getComponentModelProperties(): {[name: string]: any} {

        return {
            descending: this.state.descending,
            limitToType: this.state.limitToType,
            listOrder: this.state.listOrder,
            pageSize: this.state.pageSize,
        };
    }

    private handleChange(event: React.ChangeEvent<HTMLInputElement>): void {

        const target: HTMLInputElement = event.currentTarget;
        const idPrefix: string
            = `${this.props.containerKey}_${this.props.component.key}_`;

        switch (target.id) {
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

render(
    <PageModelEditor />,
    document.getElementById("cms-content"),
);

interface CategoryTreeComponent extends ComponentModel {

    showFullTree: boolean;
}

interface ItemListComponent extends ComponentModel {

    descending: boolean;
    limitToType: string;
    listOrder: string[];
    pageSize: number;
}

ComponentModelEditor.registerEditorComponents(
    "org.librecms.pagemodel.CategoryTreeComponent",
    {
        editorDialog:
            CategoryTreeComponentEditorDialog as typeof React.Component,
        propertiesList:
            CategoryTreeComponentPropertiesList as typeof React.Component,
    },
);

ComponentModelEditor.registerEditorComponents(
    "org.librecms.pagemodel.ItemListComponent",
    {
        editorDialog:
            ItemListComponentEditorDialog as typeof React.Component,
        propertiesList:
            ItemListComponentPropertiesList as typeof React.Component,
    },
);
