(function() {

    // the only pseudo user
    var user_id = '101';
    var user_fullname = 'John';
    var user_name = 'Infinite';
    var single_fetch_limit = 3;

    user_name = (getQueryVariable('id')) == undefined ? user_name : getQueryVariable('id');
    /**
     * Initialize
     */
    function init() {
        // Register event listeners
        $('watched-btn').addEventListener('click', loadWatchedItems);
        $('fav-btn').addEventListener('click', loadFavoriteItems);
        $('recommend-btn').addEventListener('click', loadRecommendedItems);

        var welcomeMsg = $('welcome-msg');
        welcomeMsg.innerHTML = 'Welcome, ' + user_fullname;
        loadFavoriteItems();
    }

    function getQueryVariable(variable)
{
       var query = window.location.search.substring(1);
       var vars = query.split("&");
       for (var i=0;i<vars.length;i++) {
               var pair = vars[i].split("=");
               if(pair[0] == variable){return pair[1];}
       }
       return(false);
}

    /**
     * A helper function that makes a navigation button active
     */
    function activeBtn(btnId) {
        var btns = document.getElementsByClassName('main-nav-btn');

        // deactivate all navigation buttons
        for (var i = 0; i < btns.length; i++) {
            btns[i].className = btns[i].className.replace(/\bactive\b/, '');
        }

        // active btdId
        var btn = $(btnId);
        btn.className += ' active';
    }

    function showLoadingMessage(msg) {
        var itemList = $('item-list');
        itemList.innerHTML = '<p class="notice"><i class="fas fa-spinner fa-spin"></i> ' +
            msg + '</p>';
    }

    function showWarningMessage(msg) {
        var itemList = $('item-list');
        itemList.innerHTML = '<p class="notice"><i class="fas fa-exclamation-triangle"></i> ' +
            msg + '</p>';
    }

    function showErrorMessage(msg) {
        var itemList = $('item-list');
        itemList.innerHTML = '<p class="notice"><i class="fas fa-exclamation-circle"></i> ' +
            msg + '</p>';
    }

    /**
     * A helper function that creates a DOM element <tag options...>
     * 
     * @param tag
     * @param options
     * @returns
     */
    
    function $(tag, options) {
        if (!options) {
            return document.getElementById(tag);
        }

        var element = document.createElement(tag);

        for (var option in options) {
            if (options.hasOwnProperty(option)) {
                element[option] = options[option];
            }
        }

        return element;
    }

    function hideElement(element) {
        element.style.display = 'none';
    }

    function showElement(element, style) {
        var displayStyle = style ? style : 'block';
        element.style.display = displayStyle;
    }

    /**
     * AJAX helper
     * 
     * @param method -
     *            GET|POST|PUT|DELETE
     * @param url -
     *            API end point
     * @param callback -
     *            This the successful callback
     * @param errorHandler -
     *            This is the failed callback
     */
    function ajax(method, url, data, callback, errorHandler) {
        var xhr = new XMLHttpRequest();

        xhr.open(method, url, true);

        xhr.onload = function() {
        	if (xhr.status === 200) {
        		callback(xhr.responseText);
        	} else {
        		errorHandler();
        	}
        };

        xhr.onerror = function() {
            console.error("The request couldn't be completed.");
            errorHandler();
        };

        if (data === null) {
            xhr.send();
        } else {
            xhr.setRequestHeader("Content-Type",
                "application/json;charset=utf-8");
            xhr.send(data);
        }
    }
    
    // AJAX APIs
    /**
     * API #1 Load the nearby items API end point: [GET]
     * /Dashi/search?user_id=1111&lat=37.38&lon=-122.08
     */
    function loadWatchedItems() {
        console.log('loadWatchedItems');
        activeBtn('watched-btn');

        // The request parameters
        var url = './search';
        var params = 'id=' + user_name;
        var req = JSON.stringify({});

        // display loading message
        showLoadingMessage('Loading watched Animes...');

        // make AJAX call
        ajax('GET', url + '?' + params, req,
            // successful callback
            function(res) {
                var items = JSON.parse(res);
                if (!items || items.length === 0) {
                    showWarningMessage('No watched Anime.');
                } else {
                    listItems(items);
                }
            },
            // failed callback
            function() {
                showErrorMessage('Cannot load watched Animes.');
            });
    }

    /**
     * API #2 Load favorite (or visited) items API end point: [GET]
     * /Dashi/history?user_id=1111
     */
    function loadFavoriteItems() {
        activeBtn('fav-btn');

        // The request parameters
        var url = './favorite';
        var params = 'user_id=' + user_id;
        var req = JSON.stringify({});

        // display loading message
        showLoadingMessage('Loading favorite Animes...');

        // make AJAX call
        ajax('GET', url + '?' + params, req, function(res) {
            var items = JSON.parse(res);
            if (!items || items.length === 0) {
                showWarningMessage('No favorite Anime.');
            } else {
                listItems(items);
            }
        }, function() {
            showErrorMessage('Cannot load favorite Animes.');
        });
    }

    /**
     * API #3 Load recommended items API end point: [GET]
     * /Dashi/recommendation?user_id=1111
     */
    function loadRecommendedItems() {
        activeBtn('recommend-btn');

        // The request parameters
        var url = './recommendation';
        var params = 'user_id=' + user_id + '&limit=' + single_fetch_limit;

        var req = JSON.stringify({});

        // display loading message
        showLoadingMessage('Loading recommended Animes...');

        // make AJAX call
        ajax(
            'GET',
            url + '?' + params,
            req,
            // successful callback
            function(res) {
                var items = JSON.parse(res);
                if (!items || items.length === 0) {
                    showWarningMessage('No recommended Anime. Make sure you have favorites.');
                } else {
                    listItems(items);
                }
            },
            // failed callback
            function() {
                showErrorMessage('Cannot load recommended Animes.');
            });
    }

    /**
     * API #4 Toggle favorite (or visited) items
     * 
     * @param item_id -
     *            The item business id
     * 
     * API end point: [POST]/[DELETE] /Dashi/history request json data: {
     * user_id: 1111, visited: [a_list_of_business_ids] }
     */
    function changeFavoriteItem(item_id) {
        // Check whether this item has been visited or not
        var li = $('item-' + item_id);
        var favIcon = $('fav-icon-' + item_id);
        var favorite = li.dataset.favorite !== 'true';

        // The request parameters
        var url = './favorite';
        var req = JSON.stringify({
            user_id: user_id,
            favorite: [item_id]
        });
        var method = favorite ? 'POST' : 'DELETE';

        ajax(method, url, req,
            // successful callback
            function(res) {
                var result = JSON.parse(res);
                if (result.result === 'SUCCESS') {
                    li.dataset.favorite = favorite;
                    favIcon.className = favorite ? 'fas fa-heart fa-2x' : 'far fa-heart fa-2x';
                }
            });
    }

    /**
     * List items
     * 
     * @param items -
     *            An array of item JSON objects
     */
    function listItems(items) {
        // Clear the current results
        var itemList = $('item-list');
        itemList.innerHTML = '';

        for (var i = 0; i < items.length; i++) {
            addItem(itemList, items[i]);
        }
    }

    /**
     * Add item to the list
     * 
     * @param itemList -
     *            The
     *            <ul id="item-list">
     *            tag
     * @param item -
     *            The item data (JSON object)
     */
    function addItem(itemList, item) {
        var item_id = item.id;

        // create the <li> tag, including image and a div section
        var li = $('li', {
            id: 'item-' + item.id,
            className: 'item'
        });

        // set the data attribute
        li.dataset.item_id = item_id;
        li.dataset.favorite = item.favorite;

        // item image
        if (item.imageUrl) {
            li.appendChild($('img', {
                src: item.imageUrl
            }));
        } else {
            li.appendChild($(
                'img', {
                    src: 'https://assets-cdn.github.com/images/modules/logos_page/GitHub-Mark.png'
                }))
        }
        // div section (including name, summary, genres and stars )
        var section = $('div', {});

        // title
        var title = $('a', {
            href: item.url,
            target: '_blank',
            className: 'item-name'
        });
        title.innerHTML = item.name;
        section.appendChild(title);
        
        // summary
        var p = $('p', {});
        var summary = $('span', {
            className: 'item-summary'
        });
        summary.innerHTML = item.summary.length > 50? item.summary.substring(0,250) + '......' : item.summary;
        p.appendChild(summary);
        p.appendChild(document.createElement("br"));

        //genres
        var genres = $('span', {
            className: 'item-genres'
        });
        genres.innerHTML = '<strong>Genres</strong>: ' + item.genres.join(', ');
        p.appendChild(genres);
        section.appendChild(p);

        var stars = $('div', {
            className: 'stars'
        });

        for (var i = 0; i < Math.floor(item.rating/2); i++) {
            var star = $('i', {
                className: 'fas fa-star'
            });
            stars.appendChild(star);
        }

        if (item.rating % 2 >= 1) {
            stars.appendChild($('i', {
                className: 'fas fa-star-half'
            }));
        }

        section.appendChild(stars);

        li.appendChild(section);

        // episodes & date
        var p2 = $('p',{});
        var eps = $('span', {
            className: 'item-episodes'
        });
        eps.innerHTML = 'Episodes: ' + item.eps;
        p2.appendChild(eps);
        p2.appendChild(document.createElement("br"));

        var date = $('span', {
            className: 'item-date'
        });
        date.innerHTML = 'Air Date: ' + item.date;
        p2.appendChild(date);

        li.appendChild(p2);

        // favorite link
        var favLink = $('div', {
            className: 'fav-link'
        });

        favLink.onclick = function() {
            changeFavoriteItem(item_id);
        };

        favLink.appendChild($('i', {
            id: 'fav-icon-' + item_id,
            className: item.favorite ? 'fas fa-heart fa-2x' : 'far fa-heart fa-2x'
        }));

        li.appendChild(favLink);

        itemList.appendChild(li);
    }

    init();

})();
