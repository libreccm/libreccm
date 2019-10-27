'use strict';

/*function selectAsset(button) {
 
 var assetType = button.getAttribute('data-assettype');
 var contentSection = button.getAttribute('data-contentsection');
 var target = button.getAttribute('data-target');
 
 alert("AssetSelection assetType = " + assetType
 + "; contentSection = " + contentSection
 + "; target = " + target + "; ");
 
 return false;
 }*/

function getAssetsForSelectAssetDialog(dialogId) {

    var dialog = document.querySelector('#' + dialogId);
    var type = dialog.getAttribute('data-assettype');
    var contentSection = dialog.getAttribute('data-contentsection');
    var targetId = dialog.getAttribute('data-targetId');
    var filter = document.querySelector('#' + dialogId + '-asset-filter');
    var query = filter.value;
    var dispatcherPrefix = dialog.getAttribute('data-dispatcherPrefix');

    var request = new XMLHttpRequest();
    var url = dispatcherPrefix.substring(
        0,
        dispatcherPrefix.length - "/ccm".length
        )
        + "/content-sections/"
        + contentSection
        + "/assets/";
    if (type !== null && type.length > 0) {
        url = url + "?type=" + type;
    }

    if ((type !== null && type.length > 0)
        && (query !== null && query.length > 0)) {
        url = url + "&query=" + query;
    } else if (query !== null && query.length > 0) {
        url = url + "?query=" + query;
    }

    request.open("GET", url);
    request.addEventListener('load', function (event) {
        if (request.status >= 200 && request.status < 300) {
            var assets = JSON.parse(request.responseText);
            var tableRows = "";
            var i;
            for (i = 0; i < assets.length; ++i) {
                var asset = assets[i];
                tableRows = tableRows
                    + "<tr>"
                    + "<td>"
                    + "<a href=\"#\" onclick=\"setSelectedAsset(" + asset['assetId'] + ", \'" + asset['title'] + "\', \'" + targetId + "\', \'" + dialogId + "\')\">"
                    + asset['title']
                    + "</a>"
                    + "<td>"
                    + asset['typeLabel']
                    + "</td>"
                    + "<td>" + asset['place'] + "</td>"
                    + "</tr>";
            }
            document
                .querySelector("#" + dialogId + " tbody")
                .innerHTML = tableRows;
        } else {
            alert("Error while retrieving assets. "
                + "Response code: " + request.status + " "
                + "Message: " + request.statusText);
        }
    });
    request.send();
}

function setSelectedAsset(assetId, assetTitle, targetId, dialogId) {
    var target = document.querySelector("#" + targetId);
    var targetText = document.querySelector("#" + targetId + "-selected");

    target.value = assetId;
    targetText.textContent = assetTitle;

    toggleSelectAssetDialog('hide', dialogId);

}

function toggleSelectAssetDialog(mode, dialogId) {

    var dialog = document.querySelector("#" + dialogId);

    if ('show' === mode) {
        dialog.setAttribute('open', 'open');
        getAssetsForSelectAssetDialog(dialogId);
    } else {
        dialog.setAttribute('open', 'false');
    }
}

function getItemsForSelectItemDialog(dialogId) {

    var dialog = document.querySelector('#' + dialogId);
    var type = dialog.getAttribute('data-assettype');
    var contentSection = dialog.getAttribute('data-contentsection');
    var targetId = dialog.getAttribute('data-targetId');
    var filter = document.querySelector('#' + dialogId + '-item-filter');
    var query = filter.value;
    var dispatcherPrefix = dialog.getAttribute('data-dispatcherPrefix');

    var request = new XMLHttpRequest();
    var url = dispatcherPrefix.substring(0,
        dispatcherPrefix.length - "/ccm".length) + "/content-sections/" + contentSection + "/items/?version=DRAFT";
    if (type !== null && type.length > 0) {
        url = url + "?type=" + type;
    }

    if ((type !== null && type.length > 0)
        && (query !== null && query.length > 0)) {
        url = url + "&query=" + query;
    } else if (query !== null && query.length > 0) {
        url = url + "?query=" + query;
    }

    request.open("GET", url);
    request.withCredentials = true;
    request.addEventListener('load', function (event) {
        if (request.status >= 200 && request.status <= 300) {
            var items = JSON.parse(request.responseText);
            var tableRows = "";
            var i;
            for (i = 0; i < items.length; ++i) {
                var item = items[i];
                tableRows = tableRows
                    + "<tr>"
                    + "<td>"
                    + "<a href=\"#\" onclick=\"setSelectedAsset(" + item['itemId'] + ", \'" + item['title'] + "\', \'" + targetId + "\', \'" + dialogId + "\')\">"
                    + item['title']
                    + "</a>"
                    + "</td>"
                    + "<td>" + item['place'] + "</td>"
                    + "</tr>";
            }
            document
                .querySelector('#' + dialogId + " tbody")
                .innerHTML = tableRows;
        } else {
            alert("Error while retrieving items. "
                + "Response code: " + request.status + " "
                + "Message: " + request.statusText);
        }
    });
    request.send();
}

function setSelectedItem(itemId, itemTitle, targetId, dialogId) {
    var target = document.querySelector('#' + targetId);
    var targetText = document.querySelector("#" + targetId + "-selected");

    target.value = itemId;
    targetText.textContent = itemTitle;

    toggleSelectItemDialog('hide', dialogId);
}

function toggleSelectItemDialog(mode, dialogId) {

    var dialog = document.querySelector('#' + dialogId);

    if ('show' === mode) {
        dialog.setAttribute('open', 'open');
        getItemsForSelectItemDialog(dialogId);
    } else {
        dialog.setAttribute('open', 'false');
    }
}

function getJournalsForSelectJournalDialog(dialogId) {

    var dialog = document.querySelector("#" + dialogId);
    var targetId = dialog.getAttribute('data-targetId');
    var filter = document.querySelector('#' + dialogId + '-journals-filter');
    var query = filter.value;
    var dispatcherPrefix = dialog.getAttribute('data-dispatcherPrefix');

    var request = new XMLHttpRequest();
    var url = dispatcherPrefix.substring(
        0,
        dispatcherPrefix.length - "/ccm".length
        )
        + "/scipublications/journals";

    if (query !== null && query.length > 0) {
        url = `${url}?query=${query}`;
    }

    request.open("GET", url);
    request.addEventListener("load", function (event) {
        if (request.status >= 200 && request.status < 300) {
            var journals = JSON.parse(request.responseText);
            var tableRows = "";
            var i;
            for (i = 0; i < journals.length; ++i) {
                var journal = journals[i];
                tableRows = tableRows
                    + `<tr>
    <td>
        <a href="#" 
           onclick="setSelectedJournal('${journal["journalId"]}', '${journal["title"]}', '${targetId}', '${dialogId}')">
            ${journal["title"]}
        </a>
    </td>
</tr>`;
            }
            document
                .querySelector("#" + dialogId + " tbody")
                .innerHTML = tableRows;
        } else {
            alert("Error while retrieving journals. "
                + `Response code: ${request.status} `
                + `Message: ${request.statusText}`);
        }
    });
    request.send();
}

function setSelectedJournal(journalId, journalTitle, targetId, dialogId) {
    var target = document.querySelector(`#${targetId}`);
    var targetText = document.querySelector(`#${targetId}-selected`);

    target.value = journalId;
    targetText.textContent = journalTitle;

    toggleSelectJournalDialog('hide', dialogId);
}

function toggleSelectJournalDialog(mode, dialogId) {
    var dialog = document.querySelector("#" + dialogId);

    if ('show' === mode) {
        dialog.setAttribute('open', 'open');
        getAssetsForSelectAssetDialog(dialogId);
    } else {
        dialog.setAttribute('open', 'false');
    }
}

function getItemsForSelectPublicationDialog(dialogId) {
    var dialog = document.querySelector('#' + dialogId);
    var type = dialog.getAttribute("data-publicationtype");
    var targetId = dialog.getAttribute('data-targetId');
    var filter = document.querySelector(`#${dialogId}-publication-filter`);
    var query = filter.value;
    var dispatcherPrefix = dialog.getAttribute('data-dispatcherPrefix');

    var request = new XMLHttpRequest();
    var url = `${dispatcherPrefix.substring(0,
        dispatcherPrefix.length - "/ccm".length)}/scipublications/publications`;
    if (filter !== null && type.length > 0) {
        url = `${url}?type=${type}`;
    }

    if ((type !== null && type.length > 0)
        && (query !== null && query.length > 0)) {
        url = `${url}&query=${query}`;
    } else if (query !== null && query.length > 0) {
        url = `${url}?query=${query}`;
    }

    request.open("GET", url);
    request.addEventListener("load", function (event) {
        if (request.status >= 200 && request.status < 300) {
            var publications = JSON.parse(request.responseText);
            var tableRows = "";
            var i;
            for (i = 0; i < publications.length; ++i) {
                var publication = publications[i];
                var publicationStr = `${publication.authors} (${publication.year}): ${publication.title}`;

                if (publications.publisher) {
                    publicationStr = `${publicationStr}: ${publication.place}: ${publication.publisher}`;
                }

                tableRows = tableRows
                    + `<tr>
    <td>
        <a href="#"
           onclick="setSelectedPublication('${publication.publicationId}', '${publicationStr}', '${targetId}', '${dialogId}')">
            ${publicationStr}
        </a>
    </td>
</tr>`;
            }
            document
                .querySelector(`#${dialogId} tbody`)
                .innerHTML = tableRows;
        } else {
            alert("Error while retrieving publications. "
                + "Response code: " + request.status + " "
                + "Message: " + request.statusText);
        }
    });
    request.send();
}

function setSelectedPublication(publicationId, publicationStr, targetId,
    dialogId) {
    var target = document.querySelector(`#${targetId}`);
    var targetText = document.querySelector(`#${targetId}-selected`);

    target.value = publicationId;
    targetText.textContent = publicationStr;

    toggleSelectPublicationDialog('hide', dialogId);
}

function toggleSelectPublicationDialog(mode, dialogId) {
    var dialog = document.querySelector("#" + dialogId);

    if ('show' === mode) {
        dialog.setAttribute('open', 'open');
        getAssetsForSelectAssetDialog(dialogId);
    } else {
        dialog.setAttribute('open', 'false');
    }
}

function getPublishersForSelectPublisherDialog(dialogId) {
    var dialog = document.querySelector("#" + dialogId);
    var targetId = dialog.getAttribute('data-targetId');
    var filter = document.querySelector('#' + dialogId + '-publisher-filter');
    var query = filter.value;
    var dispatcherPrefix = dialog.getAttribute('data-dispatcherPrefix');

    var request = new XMLHttpRequest();
    var url = `${dispatcherPrefix.substring(0,
        dispatcherPrefix.length - "/ccm".length)}/scipublications/publishers}`;
    if (query !== null && query.length > 0) {
        url = `${url}?query=${query}`;
    }

    request.open("GET", url);
    request.addEventListener("load", function (event) {
        if (request.status >= 200 && request.status < 300) {
            var publishers = JSON.parse(request.responseText);
            var tableRows = "";
            var i;
            for (i = 0; i < publishers.length; ++i) {
                var publisher = publishers[i];
                tableRows = tableRows
                    + `<tr>
<td>
    <a href="#"
       onclick="setSelectedPublisher('${publisher.publisherId}', '${publisher.name}, ${publisher.place}', '${targetId}', '${dialogId}')">
        ${publisher.name}, ${publisher.place}
    </a>
</td>
</tr>`;
            }
            document
                .querySelector("#" + dialogId + " tbody")
                .innerHTML = tableRows;
        } else {
            alert("Error while retrieving publishers. "
                + `Response code: ${request.status} `
                + `Message: ${request.statusText}`);
        }
    });
    request.send();
}

function setSelectedPublisher(publisherId, publisherStr, targetId, dialogId) {
    var target = document.querySelector(`#${targetId}`);
    var targetText = document.querySelector(`#${targetId}-selected`);

    target.value = publisherId;
    targetText.textContent = publisherStr;

    toggleSelectPublisherDialog("hide", dialogId);
}

function toggleSelectPublisherDialog(mode, dialogId) {
    var dialog = document.querySelector("#" + dialogId);

    if ('show' === mode) {
        dialog.setAttribute('open', 'open');
        getAssetsForSelectAssetDialog(dialogId);
    } else {
        dialog.setAttribute('open', 'false');
    }
}

function getSeriesForSelectSeriesDialog(dialogId) {
    var dialog = document.querySelector("#" + dialogId);
    var targetId = dialog.getAttribute('data-targetId');
    var filter = document.querySelector('#' + dialogId + '-series-filter');
    var query = filter.value;
    var dispatcherPrefix = dialog.getAttribute('data-dispatcherPrefix');
    
    var request = new XMLHttpRequest();
    var url = dispatcherPrefix.substring(
        0,
        dispatcherPrefix.length - "/ccm".length
        )
        + "/scipublications/series";

    if (query !== null && query.length > 0) {
        url = `${url}?query=${query}`;
    }

    request.open("GET", url);
    request.addEventListener("load", function (event) {
        if (request.status >= 200 && request.status < 300) {
            var seriesList = JSON.parse(request.responseText);
            var tableRows = "";
            var i;
            for (i = 0; i < seriesList.length; ++i) {
                var series = seriesList[i];
                tableRows = tableRows 
                    + `<tr>
    <td>
        <a href="#"
           onclick="setSelectedSeries('${series.seriesId}', '${series.name}', '${targetId}', '${dialogId}')">
            ${series.name}
        </a>
    </td>
</tr>`;
            }
            document
                .querySelector("#" + dialogId + " tbody")
                .innerHTML = tableRows;
        } else {
            alert("Error while retrieving series. "
                + `Response code: ${request.status} `
                + `Message: ${request.statusText}`);
        }
    });
    request.send();
}

function setSelectedSeries(seriesId, seriesName, targetId, dialogId) {
    var target = document.querySelector(`#${targetId}`);
    var targetText = document.querySelector(`#${targetId}-selected`);

    target.value = seriesId;
    targetText.textContent = seriesName;

    toggleSelectSeriesDialog('hide', dialogId);
}

function toggleSelectSeriesDialog(dialogId) {
    var dialog = document.querySelector("#" + dialogId);

    if ('show' === mode) {
        dialog.setAttribute('open', 'open');
        getAssetsForSelectAssetDialog(dialogId);
    } else {
        dialog.setAttribute('open', 'false');
    }
}

document.addEventListener('DOMContentLoaded', function () {

    var i;

    var buttons = document.querySelectorAll('.select-asset-button');
    for (i = 0; i < buttons.length; ++i) {

        buttons[i].addEventListener('click', function (event) {

            var button = event.currentTarget;
            var dialogId = button.getAttribute('data-dialogId');

            toggleSelectAssetDialog('show', dialogId);
            event.stopPropagation();
            return false;
        });
    }

    var closeButtons = document.querySelectorAll(
        '.asset-search-widget-dialog .close-button');
    for (i = 0; i < closeButtons.length; ++i) {

        closeButtons[i].addEventListener('click', function (event) {

            var button = event.currentTarget;
            var dialogId = button.getAttribute('data-dialogId');

            toggleSelectAssetDialog('hide', dialogId);

            event.stopPropagation();
            return false;
        });

    }

    var applyButtons = document.querySelectorAll(
        '.asset-search-widget-dialog .apply-filter');
    for (i = 0; i < applyButtons.length; ++i) {

        applyButtons[i].addEventListener('click', function (event) {

            var button = event.currentTarget;
            var dialogId = button.getAttribute('data-dialogId');

            getAssetsForSelectAssetDialog(dialogId);

            event.stopPropagation();
            return false;
        });
    }
});

document.addEventListener('DOMContentLoaded', function () {

    var i;

    var buttons = document.querySelectorAll('.select-item-button');
    for (i = 0; i < buttons.length; ++i) {

        buttons[i].addEventListener('click', function (event) {

            var button = event.currentTarget;
            var dialogId = button.getAttribute('data-dialogId');

            toggleSelectItemDialog('show', dialogId);
            event.stopPropagation();
            return false;
        });
    }

    var closeButtons = document.querySelectorAll(
        '.item-search-widget-dialog .close-button');
    for (i = 0; i < closeButtons.length; ++i) {

        closeButtons[i].addEventListener('click', function (event) {

            var button = event.currentTarget;
            var dialogId = button.getAttribute('data-dialogId');

            toggleSelectItemDialog('hide', dialogId);

            event.stopPropagation();
            return false;
        });
    }

    var applyButtons = document.querySelectorAll(
        '.item-search-widget-dialog .apply-filter');
    for (i = 0; i < applyButtons.length; ++i) {

        applyButtons[i].addEventListener('click', function (event) {

            var button = event.currentTarget;
            var dialogId = button.getAttribute('data-dialogId');

            getAssetsForSelectAssetDialog(dialogId);

            event.stopPropagation();
            return false;
        });
    }
});


