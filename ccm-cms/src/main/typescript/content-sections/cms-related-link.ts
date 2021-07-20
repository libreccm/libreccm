document.addEventListener("DOMContentLoaded", function(event) {
    const linkTypes = document.querySelectorAll(".link-type-select");

    for(let i = 0; i < linkTypes.length; i++) {
        linkTypes[i].addEventListener("selected", function(event) {
            const target = event.currentTarget as HTMLElement;
            const value = target.getAttribute("value");

            const selectedTypeElem = document.querySelector(
                `#relatedlink-target-${value}`
                );
            if (selectedTypeElem) {
                const types = document.querySelectorAll(".relatedlink-target");
                for(let j = 0; j < types.length; j++) {
                    types[j].classList.add("d-none");
                }
                selectedTypeElem.classList.remove("d-none");
            }

        });
    }
});