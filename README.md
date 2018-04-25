# I/O Codelabs open source release

This repository exists to help you open-source your code required for your Codelabs at Google I/O.

If you choose to include your code in this repo, note:

* We'll perform the go/releasing process for you: **do not** create your own launchcal
* Your public repo will be created under https://github.com/googlecodelabs/name-of-codelab

## Steps

1. Clone this repository: `git clone sso://devrel/codelabs/ospo`

2. Copy the `sample-base` folder and modify it as appropriate
  * Your code does not need to be *final*, and you can check-in work-in-progress code

3. Follow the steps in [1. Prepare](https://g3doc.corp.google.com/company/teams/opensource/releasing/index.md?cl=head#prepare) as part of the OSPO process, to prepare your code—ignore the other steps
  * Importantly, include `LICENSE`, `CONTRIBUTING.md` files and add Apache 2 license headers to all your source files
  * If you're including non-Google code, ensure it's clearly within a `third_party` folder

4. Once you're ready, check-in the code:

```bash
git add your-new-repo-name
git commit -m "Open-source code for I/O for your-new-repo-team"

# nb. you may be able to just `git push`, this skips code review
git push sso://devrel/_direct/codelabs/ospo
```

All of eng are writers on this repo, so we trust you to only contribute your code.
If your release is complex in any way, please email iocodelabs-team@ and let us know.

## Launch

We'll announce to all the authors when the OSPO process is completed.
At this point, reach out to iocodelabs-team@ if you don't yet have write access to your newly created `googlecodelabs` repo (it's not always possible to match up Googler usernames with your GitHub users).
