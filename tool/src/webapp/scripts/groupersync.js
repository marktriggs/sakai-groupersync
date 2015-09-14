// Auto populate address based on description
// FIXME: Pass this max length in from the Java code somehow.
(function (exports) {
    "use strict";

    function AutoPopulateHandler(form, maxLength) {
        this.form = form;
        this.descriptionInput = form.find('.description');
        this.addressInput = form.find('.groupId');
        this.submit = form.find('.submit-btn');

        this.requiredSuffixLength = form.find('.requiredSuffix').text().split(/@/)[0].length;
        this.lastGeneratedValue = '';
        this.invalid_char_regex = /[^a-zA-Z0-9_. ]/g;
        this.maxLength = 60;

        this.addressInput.attr('maxlength', this.calculateMaxLength());

        this.bindToEvents();
    }


    AutoPopulateHandler.prototype.calculateMaxLength = function () {
        return this.maxLength - this.requiredSuffixLength;
    };


    AutoPopulateHandler.prototype.descriptionUpdated = function () {
        if (this.addressInput.val() != '' && this.addressInput.val() != this.lastGeneratedValue) {
            // You're on your own!  Type it yourself.
            return;
        }

        this.lastGeneratedValue = this.descriptionInput.val().toLowerCase()
            .replace(this.invalid_char_regex, '')
            .replace(/\s+/g, '-')
            .substring(0, this.calculateMaxLength());

        this.addressInput.val(this.lastGeneratedValue).trigger("change");
    };


    AutoPopulateHandler.prototype.addressUpdated = function () {
        var value = this.addressInput.val();

        // Hyphens will match our invalid character regexp but they're actually
        // OK since we put them in ourselves.
        var noHyphens = value.replace(/-/g, '');

        if (new RegExp(this.invalid_char_regex).test(noHyphens) || new RegExp(/\s/).test(noHyphens)) {
            // Invalid
            this.addressInput.closest('.form-group').addClass('has-error');
            $('.invalid-address-input').show();
            this.submit.prop('disabled', true);
        } else {
            // OK
            this.addressInput.closest('.form-group').removeClass('has-error');
            $('.invalid-address-input').hide();
            this.submit.prop('disabled', false);
        }
    };

    AutoPopulateHandler.prototype.bindToEvents = function () {
        var self = this;
        this.descriptionInput.on('keyup change', function () {
            self.descriptionUpdated();
        });

        this.addressInput.on('keyup change', function () {
            self.addressUpdated();
        });

        this.form.on('submit', function (e) {
            // No submitting if the button was disabled.
            if (self.submit.prop('disabled')) {
                e.preventDefault();
                return false;
            }
        });

        this.form.find('.reset-address-btn').on('click', function () {
            self.addressInput.val('');
            self.descriptionInput.trigger('change');
            return false;
        });

    };


    exports.AutoPopulateHandler = AutoPopulateHandler;
}(this));



// Grouper Character Count
(function (exports) {
    "use strict";

    function CharacterCountHandler($input, $countMessage) {
        this.input = $input;
        this.countMessage = $countMessage;

        this.bindToEvents();
        this.updateCountMessage();
    };


    CharacterCountHandler.prototype.bindToEvents = function() {
        var self = this;
        self.input.on('keyup change', function () {
            self.updateCountMessage();
        });
    };


    CharacterCountHandler.prototype.updateCountMessage = function() {
        var count = this.input.val().length;
        var max = parseInt(this.input.attr("maxLength"));
        var remaining = max - count;

        this.countMessage.find(".characters-remaining").html(remaining);
    };

    exports.CharacterCountHandler = CharacterCountHandler;
}(this));


// Create group modal
(function (exports) {
    "use strict";

    function CreateGroupModal(baseUrl, sakaiGroupId) {
        this.sakaiGroupId = sakaiGroupId;
        this.baseUrl = baseUrl;
    }


    CreateGroupModal.prototype.show = function () {
        var template = $('#create-group-template');
        var form = $(template.html().trim());

        form.find('.create-group-form').attr('action', this.baseUrl + 'create_group');
        form.find('.sakaiGroupId').val(this.sakaiGroupId);

        $('#modal-area .modal-body').empty().append(form);
        $('#modal-area .modal-title').html('Create new group');
        $('#modal-area').modal();

        $('#modal-area').on('shown.bs.modal', function () {
            resizeFrame();
            form.find('.description').focus();
            new AutoPopulateHandler(form);
            new CharacterCountHandler(form.find(":input.description"), form.find(".description-character-count"));
            new CharacterCountHandler(form.find(":input.groupId"), form.find(".group-address-character-count"));
        });
    };


    exports.CreateGroupModal = CreateGroupModal;
}(this));


// Members modal
(function (exports) {
    "use strict";

    function MembersModal(members) {
        this.members = members;
    }


    MembersModal.prototype.show = function () {
        var container = $('<ul class="member-list" />');

        $(this.members).each(function (idx, member) {
            var listItem = $('<li />');

            listItem.append($('<div class="member"/>')
                            .append($('<span class="name" />').text(member.name))
                            .append($('<span class="eid" />').text(member.eid))
                            .append($('<span class="role" />').text(member.role)));

            container.append(listItem);
        });

        $('#modal-area .modal-body').empty().append(container);
        $('#modal-area .modal-title').html('Member list');
        $('#modal-area').modal();

        $('#modal-area').on('shown.bs.modal', resizeFrame);
    };


    exports.MembersModal = MembersModal;

}(this));



// Grouper Sync
(function (exports) {
    "use strict";

    function GrouperSync(baseUrl) {
        this.baseUrl = baseUrl;
        this.bindToEvents();
    }

    var groupFor = function(elt) {
        var group = $(elt).closest('.group-container');
        return group.data('sakai-group-id');
    };

    GrouperSync.prototype.showMembers = function (elt) {
        var self = this;
        var sakaiGroupId = groupFor(elt);

        $.ajax({
            method: 'GET',
            url: self.baseUrl + 'members',
            data: {
                sakaiGroupId: sakaiGroupId
            },
            success: function (members) {
                new MembersModal(members).show();
            }
        });
    };


    GrouperSync.prototype.bindToEvents = function () {
        var self = this;

        $(document).on('click', '.show-members-btn', function () {
            self.showMembers(this);
        });

        $(document).on('click', '.create-group-btn', function () {
            new CreateGroupModal(self.baseUrl, groupFor(this)).show();
        });

    };


    exports.GrouperSync = GrouperSync;

}(this));

