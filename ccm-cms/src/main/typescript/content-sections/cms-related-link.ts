document.addEventListener("DOMContentLoaded", function (event) {
    const elem = document.querySelector("#link-type-select");
    if (elem) {
        const linkTypeSelect: HTMLSelectElement = elem as HTMLSelectElement;
        const preSelected = linkTypeSelect.options.item(
            linkTypeSelect.selectedIndex
        );
        if (preSelected) {
            const value = preSelected.value;
            if (value != "--none--") {
                enableLinkTypeInputs(value);
            }
        }

        linkTypeSelect.addEventListener("input", function (event) {
            const target = event.currentTarget as HTMLSelectElement;
            const selected = target.options.item(
                linkTypeSelect.selectedIndex
            );
            if (selected) {
                const value = selected.value;
                if (value == "--none--") {
                    const types = document.querySelectorAll(
                        ".relatedlink-target"
                    );
                    for (let j = 0; j < types.length; j++) {
                        types[j].classList.add("d-none");
                    }
                } else {
                    enableLinkTypeInputs(value);
                }
            }
        });
    }

    const linkTypes = document.querySelectorAll(".link-type-select");

    for (let i = 0; i < linkTypes.length; i++) {
        if (linkTypes[i].getAttribute("selected") === "selected") {
            const selectedTypeElem = document.querySelector(
                `#relatedlink-target-${linkTypes[i].getAttribute("value")}`
            );
            if (selectedTypeElem) {
                const types = document.querySelectorAll(".relatedlink-target");
                for (let j = 0; j < types.length; j++) {
                    types[j].classList.add("d-none");
                }
                selectedTypeElem.classList.remove("d-none");
            }
        }

        linkTypes[i].addEventListener("input", function (event) {
            const target = event.currentTarget as HTMLElement;
            const value = target.getAttribute("value");

            const selectedTypeElem = document.querySelector(
                `#relatedlink-target-${value}`
            );
            if (selectedTypeElem) {
                const types = document.querySelectorAll(".relatedlink-target");
                for (let j = 0; j < types.length; j++) {
                    types[j].classList.add("d-none");
                }
                selectedTypeElem.classList.remove("d-none");
            }
        });
    }
});

function enableLinkTypeInputs(selectedLinkType: string): void {
    const selectedTypeElem = document.querySelector(
        `#relatedlink-target-${selectedLinkType}`
    );
    if (selectedTypeElem) {
        const types = document.querySelectorAll(".relatedlink-target");
        for (let j = 0; j < types.length; j++) {
            types[j].classList.add("d-none");
        }
        selectedTypeElem.classList.remove("d-none");
    }
}
