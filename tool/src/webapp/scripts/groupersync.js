(function (exports) {
    "use strict";

    function GrouperSync(baseUrl) {
        this.baseUrl = baseUrl;
        this.bindToEvents();
    }

    GrouperSync.prototype.bindToEvents = function () {
        var self = this;

        $(document).on('click', '.create-group-btn', function () {
            var group = $(this).closest('.group-container');
            var sakaiGroupId = group.data('sakai-group-id');

            $.ajax({
                method: 'POST',
                url: self.baseUrl + 'create_group',
                data: {
                    groupId: prompt('type a group ID'),
                    description: prompt('type a group description'),
                    sakaiGroupId: sakaiGroupId,
                },
                success: function () {
                    location.reload();
                },
            });
        });
    }

    exports.GrouperSync = GrouperSync;

}(this));

