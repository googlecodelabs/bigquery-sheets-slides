# I/O Codelabs open source release

This repository exists to help you open-source your code required for your Codelabs at Google I/O.

If you choose to include your code in this repo, note:

* We'll perform the go/releasing process for you: **do not** create your own launchcal
* Your public repo will be created under https://github.com/googlesamples/name-of-codelab

## Steps

1. Clone this repository: `git clone sso://devrel/codelabs/ospo`

2. Copy the `sample-base` folder and modify it as appropriate
  * Your code does not need to be *final*, and you can check-in work-in-progress code

3. Follow the steps in [1. Prepare](https://g3doc.corp.google.com/company/teams/opensource/releasing/index.md?cl=head#prepare) as part of the OSPO process, to prepare your code—ignore the other steps
  * Importantly, include LICENSE, CONTRIBUTING.md files and add Apache2 license headers to all your source files

4. Once you're ready, check-in the code and email iocodelabs-team@ to let us know:

```bash
git add your-new-repo-name
git commit -m "Open-source code for I/O for your-new-repo-team"
git push
```

All of eng can write to this repo, so we trust you to only contribute your code.

## Launch

We'll announce to all the authors when the OSPO process is completed.
At this point, reach out to iocodelabs-team@ if you don't yet have write access to your newly created `googlesamples` repo (it's not always possible to match up Googler usernames with your GitHub users).
