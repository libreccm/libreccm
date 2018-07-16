import * as React from "react";
import { render } from "react-dom";

import { AbstractComponentModelEditor,
         ComponentModel,
         ComponentModelEditorProps,
         ComponentModelEditorState,
         PageModelEditor,

} from "ccm-pagemodelseditor";

PageModelEditor.registerComponentModelEditor(
    "org.librecms.pagemodel.CategoryTreeComponent",
    (categoryTree) => {
        return <CategoryTreeComponentEditor
            component={categoryTree as CategoryTreeComponent} />
    }
);
PageModelEditor.registerComponentModelEditor(
    "org.librecms.pagemodel.ItemListComponent",
    (itemList) => {
        return <ItemListComponentEditor
            component={itemList as ItemListComponent} />
    }
);

render(
    <PageModelEditor />,
    document.getElementById("cms-content"),
);

interface CategoryTreeComponent extends ComponentModel {

    showFullTree: boolean;
}

interface CategoryTreeComponentEditorProps
    extends ComponentModelEditorProps<CategoryTreeComponent> {

}

interface CategoryTreeComponentEditorState
    extends ComponentModelEditorState {

    showFullTree: boolean;
}


class CategoryTreeComponentEditor
    extends AbstractComponentModelEditor<CategoryTreeComponent,
                                         CategoryTreeComponentEditorProps,
                                         CategoryTreeComponentEditorState> {

    public constructor(props: CategoryTreeComponentEditorProps) {

        super(props);

        this.setState({
            ...this.state as any,
            dialogExpanded: "dialogClosed",
            showFullTree: false,
        });
    }

    public renderPropertyList(): React.ReactFragment {

        return <React.Fragment>
            <dt>Show full tree</dt>
            <dd>{this.props.component.showFullTree}</dd>
        </React.Fragment>;
    }

    public renderEditorDialog(): React.ReactFragment {

        return <React.Fragment />;
    }
}

interface ItemListComponent extends ComponentModel {

    descending: boolean;
    limitToType: string;
    pageSize: number;
    listOrder: string[],
}

interface ItemListComponentEditorProps
    extends ComponentModelEditorProps<ItemListComponent> {

}

interface ItemListComponentEditorState
    extends ComponentModelEditorState {

    descending: boolean;
    limitToType: string;
    pageSize: number;
    listOrder: string[];
}

class ItemListComponentEditor
    extends AbstractComponentModelEditor<ItemListComponent,
                                         ItemListComponentEditorProps,
                                         ItemListComponentEditorState> {

    public constructor(props: ItemListComponentEditorProps) {

        super(props as any);

        this.setState({
            ...this.state,
            dialogExpanded: "dialogClosed",
            descending: false,
            limitToType: "",
            pageSize: 30,
            listOrder: [ "objectId", ],
        });
    }

    public renderPropertyList(): React.ReactFragment {

        return <React.Fragment>
            <dt>descending</dt>
            <dd>{this.props.component.descending}</dd>
            <dt>limitToType</dt>
            <dd>{this.props.component.limitToType}</dd>
            <dt>pageSize</dt>
            <dd>{this.props.component.pageSize}</dd>
            <dt>listOrder</dt>
            <dd>
                <ul>
                {this.props.component.listOrder.map((order) => {
                    <li>
                        {order}
                    </li>
                })}
                </ul>
            </dd>
        </React.Fragment>
    }

    public renderEditorDialog(): React.ReactFragment {

        return <React.Fragment />;
    }
}
