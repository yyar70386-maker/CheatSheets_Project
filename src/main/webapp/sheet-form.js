/**
 * Category "Other" toggle + cheat sheet form validation.
 */
(function (global) {
    function bindCategoryOther(selectId, wrapId, inputId) {
        var $select = $('#' + selectId);
        var $wrap = $('#' + wrapId);
        var $input = $('#' + inputId);

        function refresh() {
            var isOther = $select.val() === 'other';
            $wrap.toggleClass('d-none', !isOther);
            $input.prop('required', isOther);
            if (!isOther) {
                $input.val('');
            }
        }

        $select.on('change', refresh);
        refresh();
        return refresh;
    }

    global.resetSheetCategory = function (selectId, wrapId, inputId) {
        $('#' + selectId).val('');
        bindCategoryOther(selectId, wrapId, inputId)();
    };

    global.bindSheetCategoryOther = bindCategoryOther;

    global.validateSheetForm = function ($form, summernoteSelector) {
        var $note = $(summernoteSelector);
        if ($note.length) {
            syncCheatSheetEditor($note);
        }
        var code = $note.length ? $note.summernote('code') : '';
        var text = $('<div>').html(code).text().trim();
        if (!text) {
            alert('Please add some content to your cheat sheet.');
            return false;
        }
        var $cat = $form.find('.sheet-category-select');
        if ($cat.val() === 'other') {
            var custom = $form.find('[name="customCategoryName"]').val().trim();
            if (!custom) {
                alert('Please enter a name for your custom category.');
                return false;
            }
        } else if (!$cat.val()) {
            alert('Please select a category.');
            return false;
        }
        return true;
    };
})(window);
