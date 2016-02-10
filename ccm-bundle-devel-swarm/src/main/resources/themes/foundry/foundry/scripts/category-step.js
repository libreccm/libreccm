/*
 Copyright: 2006, 2007, 2008 Sören Bernstein
 
 This file is part of Mandalay.
 
 Mandalay is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 2 of the License, or
 (at your option) any later version.
 
 Mandalay is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with Mandalay.  If not, see <http://www.gnu.org/licenses/>.
 */

/* DE
 Diese Funktionen sind Teil der AJAX-Seite zum Zuweisen der Kategorien.
 */

/* EN
 These functions are part of the ajax-pages to assign categories.
 */

function colorAncestors() {
    var ancestorCategories =
            document.category.selectedAncestorCategories.value.split(",");

    for (var i = 0; i < ancestorCategories.length; ++i) {
        //alert("trying to find catSelf" + ancestorCategories[i]);
        console.log("trying to find catSelf" + ancestorCategories[i]);
        var elem = document.getElementById("catSelf" + ancestorCategories[i]);
        if (elem !== null) {
            //alert("found catSelf" + ancestorCategories[i]);
            console.log("found catSelf" + ancestorCategories[i]);
            var oldClasses = elem.className.split(" ");
            var classes = "";
            for (var j = 0; j < oldClasses.length; ++j) {
                if (oldClasses[j] !== "selectedAncestorCategory"
                        && oldClasses[j] !== "notSelectedAncestorCategory") {
                    classes = classes + " " + oldClasses[j];
                }
            }
            classes = classes + " selectedAncestorCategory";

            //alert("setting class for catSelf" + ancestorCategories[i] + " to " + classes);
            elem.className = classes;
//            if (oldClassName.indexOf("selectedAncestorCategory") === -1) {
//                elem.className = elem.className + "selectedAncestorCategory";
//            }
        }
    }
}

// DE Lade einen Kategorienzweig nach, wenn dieser aufgeklappt wird
// EN Loading a branch of categories when it is expanded
function catBranchToggle(id, selCats) {
    var elToggleTreeImage = document.getElementById("catTreeToggleImage" + id);
    var elBranch = document.getElementById("catBranch" + id);

    if (elBranch.style.display == "" || elBranch.style.display == "none") {
        if (elBranch.innerHTML == "" || elBranch.innerHTML == "...") {
            elBranch.innerHTML = "...";
            elBranch.style.display = "block";
            $(elBranch).load("load-cat.jsp", "nodeID=" + id + "&selectedCats=" + selCats, function () {
                colorAncestors()
            });
        } else {
            elBranch.style.display = "block";
        }
        elToggleTreeImage.src = elToggleTreeImage.src.replace("Expand", "Collapse");
        elToggleTreeImage.alt = "[-]";
    } else {
        elBranch.style.display = "none";
        elToggleTreeImage.src = elToggleTreeImage.src.replace("Collapse", "Expand");
        elToggleTreeImage.alt = "[+]";
    }

    colorAncestors();

    return false;
}

// DE Wechselt die Ansicht eines Kategorienzweiges
// EN Toggles display of a branch of categories
function catToggle(id, selCats) {
    var elToggleTreeImage = document.getElementById("catTreeToggleImage" + id);
    var elBranch = document.getElementById("catBranch" + id);

    if (elBranch.style.display == "" || elBranch.style.display == "none") {
        elBranch.style.display = "block";
        elToggleTreeImage.src = elToggleTreeImage.src.replace("Expand", "Collapse");
        elToggleTreeImage.alt = "[-]";
    } else {
        elBranch.style.display = "none";
        elToggleTreeImage.src = elToggleTreeImage.src.replace("Collapse", "Expand");
        elToggleTreeImage.alt = "[+]";
    }

    colorAncestors();

    return false;
}

// DE Wählt eine Kategorie aus
// EN Select a category
function catSelect(id) {
    var elWidgetHidden = document.getElementById("catWdHd");

    var found = 0;

    for (var i = 0; i < elWidgetHidden.options.length && found == 0; i++) {
        if (elWidgetHidden.options[i].value == id) {
            found = 1;
        }
    }

    if (!found) {
        var optHidden = new Option('add ' + id, id, false, true);
        elWidgetHidden.options[elWidgetHidden.options.length] = optHidden;
    }

    // DE Ändere den Link
    // EN Change link
    var elToggleLink = document.getElementById("catToggleLink" + id);
    elToggleLink.removeAttribute("onclick");
    elToggleLink.setAttribute("onclick", "catDeselect('" + id + "');");

    // DE Ändere das Icon
    // EN Change image
    var elToggleImage = document.images["catToggleImage" + id];
    elToggleImage.src = elToggleImage.src.replace("Unselected", "Selected");
    elToggleImage.alt = "[X]";

    return false;
}

// DE Macht eine Auswahl rückgängig
// EN Deselect a category
function catDeselect(id) {
    var elWidgetHidden = document.getElementById("catWdHd");

    var found = 0;

    for (var i = 0; i < elWidgetHidden.options.length; i++) {
        if (elWidgetHidden.options[i].value == id) {
            if (elWidgetHidden.options[i].text == id) {
                found = 1;
            }
            elWidgetHidden.removeChild(elWidgetHidden.options[i]);
        }
    }

    if (found) {
        var optHidden = new Option('del ' + id, id, false, true);
        elWidgetHidden.options[elWidgetHidden.options.length] = optHidden;
    }

// DE Ändert den Link
    // EN Change link
    var elToggleLink = document.getElementById("catToggleLink" + id);
    elToggleLink.removeAttribute("onclick");
    elToggleLink.setAttribute("onclick", "catSelect('" + id + "');");

    // DE Ändert das Icon
    // EN Change image
    var elToggleImage = document.images["catToggleImage" + id];
    elToggleImage.src = elToggleImage.src.replace("Selected", "Unselected");
    elToggleImage.alt = "[ ]";

    return false;
}
