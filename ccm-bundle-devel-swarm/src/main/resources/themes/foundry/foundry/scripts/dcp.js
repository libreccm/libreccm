function doubleClickProtect(element) {

    if (element.nodeName == "INPUT") {

        // change button label
        elementClone = element.cloneNode(true);
        elementClone.value = "Bitte warten...";

        // set cloned button and hide the original one
        element.parentNode.insertBefore(elementClone, element);
        element.style.display = "none";

        // disable all submit buttons in this form
        formElements = element.form.elements;
        for (i = 0; i < formElements.length; i++) {
            if (formElements[i].tagName == "INPUT" &&
                    formElements[i].type == "submit" &&
                    formElements[i] != element)
                formElements[i].setAttribute("disabled", "disabled");
        }

    } else {

        // disable link
        link = element.getAttribute("href");
        element.text = "Bitte warten...";
        element.removeAttribute("href");
        location.href = link;
    }

}