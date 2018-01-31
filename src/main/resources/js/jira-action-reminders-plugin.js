/* global AJS */

function validateForm() {
    AJS.$("#AdskActionRemindersMessage").html("");
    if(AJS.$("#configType").val() === ""){
        AJS.$("#AdskActionRemindersMessage").html("<span style='color: red;'>Config Type Input required!</span>");
        return false;
    }else if(AJS.$("input[name=configType]:checked").val() === "action" && AJS.$("#issueAction").val() === ""){
        AJS.$("#AdskActionRemindersMessage").html("<span style='color: red;'>Issue Action Input required!</span>");
        return false;        
    }else if(AJS.$("#query").val() === ""){
        AJS.$("#AdskActionRemindersMessage").html("<span style='color: red;'>JQL Query Input required!</span>");
        return false;
    }else if(AJS.$("#runAuthor").val() === ""){
        AJS.$("#AdskActionRemindersMessage").html("<span style='color: red;'>Run Author Input required!</span>");
        return false;
    }else if(AJS.$("#cronSchedule").val() === ""){
        AJS.$("#AdskActionRemindersMessage").html("<span style='color: red;'>Cron Schedule Input required!</span>");
        return false;
    }else if(AJS.$("#message").val() === ""){
        AJS.$("#AdskActionRemindersMessage").html("<span style='color: red;'>Message Input required!</span>");
        return false;
    }
    return true;
}

function adsk_actionremindersconfig_filter(element) {
  // Declare variables 
  var count, input, filter, table, tr, i;
  count = document.getElementById("adsk_actionremindersconfig_count");
  input = document.getElementById("adsk_actionremindersconfig_search");
  filter = input.value.toUpperCase();
  table = document.getElementById("adsk_actionreminders_configs");
  tr = table.getElementsByTagName("tr");

  // Loop through all table rows, and hide those who don't match the search query
  var size = 0;
  for (i = 0; i < tr.length; i++) {
    var td0 = tr[i].getElementsByTagName("td")[1];
    var td1 = tr[i].getElementsByTagName("td")[2];
    var td2 = tr[i].getElementsByTagName("td")[3];
    var td3 = tr[i].getElementsByTagName("td")[4];
    var td4 = tr[i].getElementsByTagName("td")[5];
    if (td0 || td1 || td2 || td3 || td4) {
      if (td0.innerHTML.toUpperCase().indexOf(filter) > -1 || 
              td1.innerHTML.toUpperCase().indexOf(filter) > -1 || 
              td2.innerHTML.toUpperCase().indexOf(filter) > -1 || 
              td3.innerHTML.toUpperCase().indexOf(filter) > -1 ||
              td4.innerHTML.toUpperCase().indexOf(filter) > -1) {
        tr[i].style.display = "";
        size++;
      } else {
        tr[i].style.display = "none";
      }
    }
  }
  count.innerHTML = size;
}