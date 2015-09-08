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

        $(this.members).each(function (idx, member) {
            var listItem = $('<li />');

            listItem.append($('<div class="member"/>')
                            .append($('<span class="name" />').text(member.name))
                            .append($('<span class="eid" />').text(member.eid))
                            .append($('<span class="role" />').text(member.role)));

            container.append(listItem);
        });

        $(this.members).each(function (idx, member) {
            var listItem = $('<li />');

            listItem.append($('<div class="member"/>')
                            .append($('<span class="name" />').text(member.name))
                            .append($('<span class="eid" />').text(member.eid))
                            .append($('<span class="role" />').text(member.role)));

            container.append(listItem);
        });

        $(this.members).each(function (idx, member) {
            var listItem = $('<li />');

            listItem.append($('<div class="member"/>')
                            .append($('<span class="name" />').text(member.name))
                            .append($('<span class="eid" />').text(member.eid))
                            .append($('<span class="role" />').text(member.role)));

            container.append(listItem);
        });


        $('#modal-area .modal-body').empty().append(container);
        $('#modal-area').modal({
            keyboard: true
        });
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

    GrouperSync.prototype.createGroup = function (elt, callback) {
        var self = this;
        var sakaiGroupId = groupFor(elt);

        $.ajax({
            method: 'POST',
            url: self.baseUrl + 'create_group',
            data: {
                groupId: prompt('type a group ID'),
                description: prompt('type a group description'),
                sakaiGroupId: sakaiGroupId
            },
            success: callback
        });
    };


    GrouperSync.prototype.bindToEvents = function () {
        var self = this;

        $(document).on('click', '.create-group-btn', function () {
            self.createGroup(this, function () {
                location.reload();
            });
        });

        $(document).on('click', '.show-members-btn', function () {
            self.showMembers(this);
        });
    };


    exports.GrouperSync = GrouperSync;

}(this));

