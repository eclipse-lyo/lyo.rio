# Contributing to Eclipse Lyo

This guide is to help you get started contributing to the Eclipse Lyo
project.

## Getting started

In order to get your commit merged into master, you need to follow a few steps.

**Before working on the code**

* Register a [new Eclipse account](https://accounts.eclipse.org/)
* Search the [existing bug list](https://bugs.eclipse.org/bugs/query.cgi?format=specific). Look for bugs with ANY status under LYO product and if none matches your problem, [file a new bug](https://bugs.eclipse.org/bugs/enter_bug.cgi?product=Lyo) on Bugilla **before working on your contribution**.
* If you want to discuss your idea before (and after) filing a bug, join the [lyo-dev mailing list](https://dev.eclipse.org/mailman/listinfo/lyo-dev).
* Accept and sign the [ECA](https://www.eclipse.org/legal/ECA.php). Instructions on how to sign are on the right side of the page.

**Developing a change**

* [Find the corresponding Gerrit project](https://git.eclipse.org/r/#/admin/projects/?filter=lyo) and clone it using the command to *Clone with `commit-msg` hook*.
* Create a new Git brach from `master` (best practice is to create a branch `b12345-gitignore` for a bug *Bug 12345 .gitignore file missing*).
* Make your changes to the code.
* Make sure you update the license header of the files you modify.
* **Make a single commit containing all your changes.**
* Start the first line of your commit message with *Bug 12345 -* for a patch that is addressing *Bug 12345*.
* Make sure to [include Singed-off-by](https://stackoverflow.com/questions/13457203/how-to-add-the-signed-off-by-field-in-the-git-patch) line in your commit message (by doing this, you are signing off on a [Developer Certificate of Origin](https://www.eclipse.org/legal/DCO.php)). **Commits without a Singed-off-by line are not accepted!**

> If you have accidentally made more than one commit, you need to [squash the commits](https://stackoverflow.com/questions/5189560/squash-my-last-x-commits-together-using-git). If you need to update your commit (in case you forgot something or received a feedback you need to address), [amend your commit](https://www.atlassian.com/git/tutorials/rewriting-history#git-commit--amend). [More Git tips are on Lyo wiki]
(https://wiki.eclipse.org/Lyo/GitTips)


**Submitting the change for review**

* Submit your commit for the review:
  * [Via Eclipse EGit](http://www.vogella.com/tutorials/Gerrit/article.html#gerritreview_configuration)
  * Via the command line: `git push origin HEAD:refs/for/master`. [git-review](https://www.mediawiki.org/wiki/Gerrit/git-review) is recommended for frequent users.

See [Gerrit User Guide](https://git.eclipse.org/r/Documentation/intro-user.html) for more information.
