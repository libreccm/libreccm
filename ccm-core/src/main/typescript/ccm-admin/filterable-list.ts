/**
 * Not ready yet. Don' use
 */

export function initFilterables(): void {
    document
        .querySelectorAll("*[data-filter]")
        .forEach(filterable => initFilterable(filterable));
}

function buildList(
    filterable: Element,
    options: Record<string, string>[],
    template: HTMLTemplateElement,
    filterInput: HTMLInputElement,
    filterBy: string[]
) {
    console.log("(Re-)Building list...");
    console.dir(filterOptions);
    filterable.innerHTML = "";

    const filteredOptions = filterInput.value
        ? filterOptions(options, filterInput.value, filterBy)
        : options;

    for (const option of filteredOptions) {
        const item = template.content.cloneNode(true);
        replacePlaceholders(item, option);
        filterable.appendChild(item);
    }
}

function filterOption(
    option: Record<string, string>,
    filter: string,
    filterBy: string[]
) {
    let result: boolean = false;

    for (const filterByProp of filterBy) {
        result = result || option[filterByProp].indexOf(filter) !== -1;
    }
    return result;
}

function filterOptions(
    options: Record<string, string>[],
    filterValue: string,
    filterBy: string[]
) {
    return options.filter(option =>
        filterOption(option, filterValue, filterBy)
    );
}

function getFilterBy(filterable): string[] {
    const filterByValue: string = filterable.getAttribute("data-filter-by");
    if (filterByValue) {
        return filterByValue.split(",");
    } else {
        return [];
    }
}

function getFilterInput(filterable): HTMLInputElement {
    const filterInputId: string = filterable.getAttribute("data-filter");
    return document.querySelector(`input${filterInputId}`);
}

function getOptions(filterable: Element): Record<string, string>[] {
    const attrValue: string = filterable.getAttribute("data-options");

    if (attrValue.startsWith("#")) {
        const dataScript: Element = document.querySelector(
            `script${attrValue}`
        );
        return JSON.parse(dataScript.textContent);
    } else {
        return JSON.parse(attrValue);
    }
}

function getTemplate(filterable: Element): HTMLTemplateElement {
    const templateId: string = filterable.getAttribute("data-template");
    return document.querySelector(templateId);
}

function initFilterable(filterable: Element): void {
    const options: Record<string, string>[] = getOptions(filterable);
    const template = getTemplate(filterable);
    const filterInput = getFilterInput(filterable);
    const filterBy = getFilterBy(filterable);

    filterInput.addEventListener("keyup", event =>
        buildList(filterable, options, template, filterInput, filterBy)
    );

    buildList(filterable, options, template, filterInput, filterBy);
}

function replacePlaceholders(node: Node, data: Record<string, string>) {
    switch (node.nodeType) {
        case Node.ELEMENT_NODE: {
            const childNodes = node.childNodes;
            for (let i = 0; i < childNodes.length; i++) {
                replacePlaceholders(childNodes[i], data);
            }
            break;
        }
        case Node.TEXT_NODE: {
            for (const key in data) {
                console.log(`replacing ${key} with ${data[key]}`);
                node.textContent = node.textContent.replace(
                    `{{${key}}}`,
                    data[key]
                );
            }
            break;
        }
        case Node.CDATA_SECTION_NODE:
            return;
        case Node.DOCUMENT_NODE: {
            const childNodes = node.childNodes;
            for (let i = 0; i < childNodes.length; i++) {
                replacePlaceholders(childNodes[i], data);
            }
            break;
        }
        case Node.DOCUMENT_FRAGMENT_NODE: {
            const childNodes = node.childNodes;
            for (let i = 0; i < childNodes.length; i++) {
                replacePlaceholders(childNodes[i], data);
            }
            break;
        }
        default:
            return;
    }
}
