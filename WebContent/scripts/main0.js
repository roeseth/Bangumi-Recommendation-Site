(function() {

    /**
     * Variables
     */
    var user_id = '101';
    var user_fullname = 'JB';
    
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
        loadWatchedItems();
    }
    
    function loadWatchedItems() {
    	activeBtn('watched-btn');
    	var watchedItems = mockSearchResponse;
    	listItems(watchedItems);
    }
    
    function loadFavoriteItems() {
    	activeBtn('fav-btn');
    	var favoriteItems = mockSearchResponse.slice(3, 6);
    	listItems(favoriteItems);
    }
    
    function loadRecommendedItems() {
    	activeBtn('recommend-btn');
    	var recommendedItems = mockSearchResponse.slice(10);
    	listItems(recommendedItems);
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
        summary.innerHTML = item.summary;
        p.appendChild(summary);
        p.appendChild(document.createElement("br"));

        //genres
        var genres = $('span', {
            className: 'item-genres'
        });
        genres.innerHTML = 'Genres: ' + item.genres.join(', ');
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

        favLink.appendChild($('i', {
            id: 'fav-icon-' + item_id,
            className: item.favorite ? 'fas fa-heart fa-2x' : 'far fa-heart fa-2x'
        }));

        li.appendChild(favLink);

        itemList.appendChild(li);
    }
    
    init();
    
})();
