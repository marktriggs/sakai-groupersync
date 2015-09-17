// Auto populate address based on description
(function (exports) {
    "use strict";

    function AutoPopulateHandler(form, config) {
        this.form = form;
        this.descriptionInput = form.find('.description');
        this.addressInput = form.find('.address');
        this.submit = form.find('.submit-btn');

        this.requiredSuffixLength = form.find('.requiredSuffix').text().split(/@/)[0].length;
        this.lastGeneratedValue = '';
        this.invalid_char_regex = new RegExp("[^" + config.addressAllowedCharacters + config.whitespaceReplacementCharacter + "]", 'g');
        this.whitespace_replacement_char = config.whitespaceReplacementCharacter;
        this.maxLength = config.maxAddressLength;

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
            .replace(/\s+/g, this.whitespace_replacement_char)
            .replace(this.invalid_char_regex, '')
            .substring(0, this.calculateMaxLength());

        this.addressInput.val(this.lastGeneratedValue).trigger("change");
    };


    AutoPopulateHandler.prototype.addressUpdated = function () {
        var value = this.addressInput.val();

        // Hyphens will match our invalid character regexp but they're actually
        // OK since we put them in ourselves.
        var noHyphens = value.replace(new RegExp(this.whitespace_replacement_char, 'g'), '');

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

        this.descriptionInput.trigger('change');

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
        this.config = config;

        $countMessage.show();
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

    function CRUDModal(baseUrl, config) {
        this.baseUrl = baseUrl;
        this.config = config;
    }


    CRUDModal.prototype.showCreateForm = function (groupContainer) {
        var self = this;
        var template = $($('#crud-template').html().trim());
        var sakaiGroupId = groupContainer['sakaiGroupId'];

        template.find('.create-group-form').attr('action', this.baseUrl + 'create_group');
        template.find('.sakaiGroupId').val(sakaiGroupId);
        template.find(':input.description').val(groupContainer['sakaiGroupTitle'].substring(0, this.config.maxDescriptionLength));

        template.find('.create-group-form .clear-btn').on('click', function () {
            template.find(':input.address').val('');
            template.find(':input.description').val('').trigger('change');

            return false;
        });

        // Quick check to see if the group is OK prior to submitting.
        template.find('.create-group-form .submit-btn').on('click', function () {
            var address = template.find(":input.address");

            address.closest('.form-group').removeClass('has-error');
            $('.address-in-use').hide();

            $.ajax({
                method: 'GET',
                url: self.baseUrl + 'check_group',
                data: {
                    groupId: address.val()
                },
                success: function () {
                    template.find('.create-group-form').submit();
                },
                error: function () {
                    address.closest('.form-group').addClass('has-error');
                    $('.address-in-use').show();
                }
            });

            return false;
        });


        $('#modal-area .modal-body').empty().append(template);
        $('#modal-area .modal-title').html('Create new group');
        $('#modal-area').modal();

        $('#modal-area').on('shown.bs.modal', function () {
            resizeFrame();
            template.find('.description').focus();
            new AutoPopulateHandler(template, self.config);
            new CharacterCountHandler(template.find(":input.description"), template.find(".description-character-count"));
            new CharacterCountHandler(template.find(":input.address"), template.find(".group-address-character-count"));
        });
    };


    CRUDModal.prototype.showDeleteButton = function (template, groupContainer) {
        var self = this;

        template.find('.delete-group-form').attr('action', this.baseUrl + 'delete_group');
        template.find('.delete-group-form .sakaiGroupId').val(groupContainer['sakaiGroupId']);

        template.find('.delete-group-form').show();
    };


    CRUDModal.prototype.showEditForm = function (groupContainer) {
        var template = $($('#crud-template').html().trim());

        template.find('.create-group-form').attr('action', this.baseUrl + 'update_group');
        template.find('.create-group-form .sakaiGroupId').val(groupContainer['sakaiGroupId']);
        template.find('.create-group-form :input.description').val(groupContainer['label']);

        template.find('.create-group-form :input.address').closest('.input-group').removeClass('input-group')
        template.find('.create-group-form :input.address').val(groupContainer['address']).prop('readonly', true);
        template.find('.create-group-form .input-append').remove();

        this.showDeleteButton(template, groupContainer);

        $('#modal-area .modal-body').empty().append(template);
        $('#modal-area .modal-title').html('Edit group');
        $('#modal-area').modal();

        $('#modal-area').on('shown.bs.modal', function () {
            resizeFrame();
            template.find('.description').focus();
            new CharacterCountHandler(template.find(":input.description"), template.find(".description-character-count"));
        });
    };

    exports.CRUDModal = CRUDModal;
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

    function GrouperSync(baseUrl, config) {
        this.baseUrl = baseUrl;
        this.bindToEvents();
        this.config = config;
    }

    var dataFor = function (row) {
        return $(row).closest('.group-container').data();
    };

    GrouperSync.prototype.showMembers = function (groupContainer) {
        var self = this;
        var sakaiGroupId = groupContainer['sakaiGroupId'];

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
            self.showMembers(dataFor(this));
        });

        $(document).on('click', '.create-group-btn', function () {
            new CRUDModal(self.baseUrl, config).showCreateForm(dataFor(this));
        });

        $(document).on('click', '.edit-btn', function () {
            new CRUDModal(self.baseUrl, config).showEditForm(dataFor(this));
        });
    };


    exports.GrouperSync = GrouperSync;

}(this));

