// Auto populate address based on description
// FIXME: Pass this max length in from the Java code somehow.
(function (exports) {
    "use strict";

    function AutoPopulateHandler(form, maxLength) {
        this.descriptionInput = form.find('.description');
        this.addressInput = form.find('.groupId');
        this.requiredSuffixLength = form.find('.requiredSuffix').text().split(/@/)[0].length;
        this.lastGeneratedValue = '';
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
            .replace(/[^a-zA-Z0-9_. ]/g, '')
            .replace(/ +/g, '-')
            .substring(0, this.calculateMaxLength());

        this.addressInput.val(this.lastGeneratedValue);
    };


    AutoPopulateHandler.prototype.bindToEvents = function () {
        var self = this;
        this.descriptionInput.on('keyup change', function () {
            self.descriptionUpdated();
        });
    };


    exports.AutoPopulateHandler = AutoPopulateHandler;
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
            form.find('.description').focus();
            new AutoPopulateHandler(form);
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

