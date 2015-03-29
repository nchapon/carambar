;; This buffer is for notes you don't want to save, and for Lisp evaluation.
;; If you want to create a file, visit that file with C-x C-f,
;; then enter the text in that file's own buffer.



(require 'url-vars)
(require 'url-dav)

(require 'json)

(with-current-buffer
    (url-retrieve-synchronously "http://localhost:3000/classes?name=List")
  (goto-char url-http-end-of-headers)
  (let* ((json-object-type 'plist)
        (rtnval (json-read)))
    (kill-buffer (current-buffer))
    rtnval))



;; (with-current-buffer (url-retrieve-synchronously "http://localhost:3000/")
;;     (goto-char url-http-end-of-headers)
;;     (setq json-array-type 'vector
;;           json-object-type 'alist)

;;     (let ((json-readtable-old (when readtable
;;                     (let ((r json-readtable))
;;                       (setq json-readtable readtable)
;;                       r)))
;;           (rtnval (json-read)))
;;       (kill-buffer (current-buffer))
;;       (when readtable (setq json-readtable json-readtable-old))
;;       rtnval))
