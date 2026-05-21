/**
 * Summernote Rich Text Editor ကို စနစ်တကျ Initialize လုပ်ပြီး 
 * ကြီးမားသော ဓာတ်ပုံများကို Ajax ဖြင့် နောက်ကွယ်မှ Upload တင်ပေးမည့် Config ဖြစ်သည်။
 */
function initCheatSheetEditor(selector) {
    $(selector).summernote({
        height: 450,
        minHeight: 300,
        maxHeight: 800,
        focus: false,
        toolbar: [
            ['style', ['style']],
            ['font', ['bold', ['underline'], 'clear']],
            ['color', ['color']],
            ['para', ['ul', ['ol'], 'paragraph']],
            ['table', ['table']],
            ['insert', ['link', 'picture', 'video']],
            ['view', ['fullscreen', 'codeview', 'help']]
        ],
        callbacks: {
            // 📸 ပုံကို Drag & Drop လုပ်ပြီး ထည့်လျှင်သော်လည်းကောင်း၊ ရွေးထည့်လျှင်သော်လည်းကောင်း အလုပ်လုပ်မည့်နေရာ
            onImageUpload: function(files) {
                if (files.length > 0) {
                    for (var i = 0; i < files.length; i++) {
                        uploadSummernoteImage(files[i], selector);
                    }
                }
            },
            // 📋 สာမျက်နှာအပြင်က ပုံကို ကူးယူပြီး (Ctrl+V) ဖြင့် Paste လုပ်လျှင် အလုပ်လုပ်မည့်နေရာ
            onPaste: function(e) {
                var clipboardData = (e.originalEvent || e).clipboardData;
                if (clipboardData && clipboardData.files && clipboardData.files.length > 0) {
                    // 🛠️ ပုံကို Copy ယူပြီး တိုက်ရိုက် Paste လုပ်ခဲ့လျှင် ၎င်းပုံကို ဖမ်းယူ၍ Upload တင်ပေးခြင်း
                    e.preventDefault();
                    uploadSummernoteImage(clipboardData.files[0], selector);
                    return;
                }
                
                // စာသားသက်သက်ဆိုလျှင် ပုံမှန်အတိုင်း ထည့်ခွင့်ပြုသည်
                var bufferText = (clipboardData || window.clipboardData).getData('Text');
                if (bufferText) {
                    e.preventDefault();
                    setTimeout(function() {
                        document.execCommand('insertText', false, bufferText);
                    }, 10);
                }
            }
        }
    });
}

/**
 * ဓာတ်ပုံကို Server ဆီသို့ Ajax ဖြင့် ပို့ဆောင်ပြီး ပုံ၏ ရလဒ် URL အား Editor အတွင်း ပြန်လည်အစားထိုးခြင်း
 */
function uploadSummernoteImage(file, selector) {
    // 🛠️ ပြင်ဆင်ချက် - startWith ကို startsWith ဟု စာလုံးပေါင်း မှန်ကန်အောင် ပြင်ဆင်ထားပါသည်
    if (file.type && !file.type.startsWith('image/')) {
        alert("ဓာတ်ပုံဖိုင်များသာ Upload တင်ခွင့်ရှိသည်။");
        return;
    }

    var data = new FormData();
    // သင့်ရဲ့ SheetImageUploadHandler က ဖတ်မယ့် Key အမည်
    data.append("image", file); 

    // Fragment ထဲတွင် သတ်မှတ်ခဲ့သော URL ကို ယူသုံးခြင်း
    var uploadUrl = window.CHEAT_SHEET_UPLOAD_URL || 'sheet-image-upload';

    // Upload တင်နေစဉ် UI ပြသခြင်း
    var $statusNode = $('<div class="alert alert-info py-2 small my-2"><i class="fas fa-spinner fa-spin me-2"></i>Uploading image... Please wait.</div>');
    $(selector).siblings('.note-editor').before($statusNode);

    $.ajax({
        url: uploadUrl,
        type: 'POST',
        data: data,
        cache: false,
        contentType: false,
        processData: false,
        success: function(response) {
            $statusNode.remove(); // Loading ဖြုတ်ခြင်း
            
            // Server ဘက်မှ URL ပြန်လာလျှင် Editor ထဲသို့ Insert လုပ်ခြင်း
            if (response && response.url) {
                $(selector).summernote('insertImage', response.url, function($image) {
                    $image.css('max-width', '100%'); // Responsive ဖြစ်အောင် ပုံစံချခြင်း
                    $image.attr('alt', 'CheatSheet Image');
                });
            } else if (response && response.error) {
                alert("Upload လုပ်၍ မရပါ- " + response.error);
            } else {
                alert("Upload လုပ်၍ မရပါ- Unknown error occurred.");
            }
        },
        error: function(jqXHR, textStatus, errorThrown) {
            $statusNode.remove();
            console.error("Upload error details:", jqXHR.responseText);
            alert("ဆာဗာသို့ ပုံပေးပို့ရာတွင် အမှားအယွင်းရှိနေသည်။ (Status: " + jqXHR.status + ")");
        }
    });
}

// String prototype helper
if (!String.prototype.startsWith) {
    String.prototype.startsWith = function(searchString, position) {
        position = position || 0;
        return this.indexOf(searchString, position) === position;
    };
}