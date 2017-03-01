	
// can be used to ensure that input values will be valid when used as part of urls
// 
function urlize(title) {
        var result = "";
        for (var i = 0; i < title.length; i++) {
            result = result + substitute(title.charAt(i));
        }
        return escape(result);
    }

    function substitute(c) {
        var sourceChars="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_ &/";
        var targetChars="0123456789abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz-_---";
         
        var indexChar = sourceChars.indexOf(c);
        if (indexChar > -1) {
            return targetChars.charAt(indexChar);
        } else {
            // Replacement list for special characters
            switch(c) {

                case "Ä": return "ae";
                          break;

                case "ä": return "ae";
                          break;

                case "Ö": return "oe";
                          break;

                case "ö": return "oe";
                          break;

                case "Ü": return "ue";
                          break;

                case "ü": return "ue";
                          break;

                case "ß": return "ss";
                          break;

                default:  return "";
            }
        }
   }